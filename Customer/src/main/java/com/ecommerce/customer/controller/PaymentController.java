package com.ecommerce.customer.controller;

import com.ecommerce.customer.config.PaymentConfig;
import com.ecommerce.library.dto.PaymentResDTO;
import com.ecommerce.library.model.Order;
import com.ecommerce.library.repository.OrderRepository;
import com.ecommerce.library.service.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class PaymentController {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderService orderService;

    @GetMapping("/create-payment")
    public String createPayment(HttpServletRequest req, HttpServletResponse resp, @RequestParam(value = "orderId") Long orderId, Principal principal) throws ServletException, IOException {
        if (principal == null) {
            return "redirect:/login";
        }

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";

//        long amount = Integer.parseInt(req.getParameter("amount")) * 100;
        Order order = orderRepository.getById(orderId);
        long amount = (order.getTotalPrice() + 3000) * 100;

        String bankCode = req.getParameter("bankCode");

        String vnp_TxnRef = order.getId().toString();
        String vnp_IpAddr = PaymentConfig.getIpAddress(req);

        String vnp_TmnCode = PaymentConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = req.getParameter("language");
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        vnp_Params.put("vnp_ReturnUrl", PaymentConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = PaymentConfig.hmacSHA512(PaymentConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = PaymentConfig.vnp_PayUrl + "?" + queryUrl;
//        com.google.gson.JsonObject job = new JsonObject();
//        job.addProperty("code", "00");
//        job.addProperty("message", "success");
//        job.addProperty("data", paymentUrl);
//        Gson gson = new Gson();
//        resp.getWriter().write(gson.toJson(job));

        return "redirect:" + paymentUrl;
    }

    @GetMapping("/payment-result")
    public String transaction(@RequestParam(value = "vnp_Amount", required = false) String amount,
                              @RequestParam(value = "vnp_BankCode", required = false) String bankCode,
                              @RequestParam(value = "vnp_OrderInfo", required = false) String orderInfo,
                              @RequestParam(value = "vnp_ResponseCode", required = false) String responseCode,
                              @RequestParam(value = "vnp_TxnRef", required = false) String txnRef,
                              Principal principal,
                              Model model) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            amount = String.valueOf(Long.parseLong(amount)/100);
            model.addAttribute("amount", amount);
            model.addAttribute("bankCode", bankCode);
            model.addAttribute("orderInfo", orderInfo);
            model.addAttribute("responseCode", responseCode);
            model.addAttribute("txnRef", txnRef);

            if(responseCode.equals("00"))
                orderService.changePaymentStatus(responseCode, Long.parseLong(txnRef));

            return "payment-result";
        }
    }
}
