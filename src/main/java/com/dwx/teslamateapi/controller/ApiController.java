package com.dwx.teslamateapi.controller;

import cn.hutool.core.util.CoordinateUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

@Log4j2
@Controller
public class ApiController {

    @ResponseBody
    @GetMapping(value = {"**"})
    public String hello(HttpServletRequest request) {
        log.info(request.getRequestURI());
        return HttpUtil.get("http://192.168.5.254:8081" + request.getRequestURI());
    }

    @ResponseBody
    @GetMapping(value = {"/api/v1/cars/{car_id}/status"})
    public String status(HttpServletRequest request) {
        log.info(request.getRequestURI());
        String res = HttpUtil.get("http://192.168.5.254:8081" + request.getRequestURI());
        JSONObject json = JSONUtil.parseObj(res);
        JSONObject car_geodata = json.getJSONObject("data").getJSONObject("status").getJSONObject("car_geodata");

        BigDecimal latitude = car_geodata.getBigDecimal("latitude");
        BigDecimal longitude = car_geodata.getBigDecimal("longitude");
        log.info("{},{}", latitude, longitude);

        CoordinateUtil.Coordinate coordinate = CoordinateUtil.wgs84ToGcj02(longitude.doubleValue(), latitude.doubleValue());
        log.info("{},{}", coordinate.getLat(), coordinate.getLng());

        car_geodata.set("latitude", coordinate.getLat());
        car_geodata.set("longitude", coordinate.getLng());

        JSONObject location = car_geodata.getJSONObject("location");
        location.set("latitude", coordinate.getLat());
        location.set("longitude", coordinate.getLng());

        return JSONUtil.toJsonStr(json);
    }

    @ResponseBody
    @GetMapping(value = {"/api/v1/cars/{car_id}/drives/{id}"})
    public String drives(HttpServletRequest request) {
        log.info(request.getRequestURI());
        String res = HttpUtil.get("http://192.168.5.254:8081" + request.getRequestURI());
        JSONObject json = JSONUtil.parseObj(res);
        JSONArray jsonArray = json.getJSONObject("data").getJSONObject("drive").getJSONArray("drive_details");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject drive_detail = jsonArray.getJSONObject(i);
            BigDecimal latitude = drive_detail.getBigDecimal("latitude");
            BigDecimal longitude = drive_detail.getBigDecimal("longitude");
            CoordinateUtil.Coordinate coordinate = CoordinateUtil.wgs84ToGcj02(longitude.doubleValue(), latitude.doubleValue());
            drive_detail.set("latitude", coordinate.getLat());
            drive_detail.set("longitude", coordinate.getLng());
        }
        return JSONUtil.toJsonStr(json);
    }
}
