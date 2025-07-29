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
import org.springframework.web.bind.annotation.PathVariable;
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
    @GetMapping(value = {"/api/v1/cars/1/status"})
    public String status(HttpServletRequest request) {
        log.info(request.getRequestURI());
        String res = HttpUtil.get("http://192.168.5.254:8081/api/v1/cars/1/status");
//        String res = "{\"data\":{\"car\":{\"car_id\":1,\"car_name\":\"Y\"},\"status\":{\"display_name\":\"Y\",\"state\":\"offline\",\"state_since\":\"2025-07-29T09:37:23+08:00\",\"odometer\":34077.18,\"car_status\":{\"healthy\":true,\"locked\":true,\"sentry_mode\":false,\"windows_open\":false,\"doors_open\":false,\"driver_front_door_open\":false,\"driver_rear_door_open\":false,\"passenger_front_door_open\":false,\"passenger_rear_door_open\":false,\"trunk_open\":false,\"frunk_open\":false,\"is_user_present\":false,\"center_display_state\":0},\"car_details\":{\"model\":\"Y\",\"trim_badging\":\"74D\"},\"car_exterior\":{\"exterior_color\":\"PearlWhite\",\"spoiler_type\":\"None\",\"wheel_type\":\"Apollo19MetallicShadow\"},\"car_geodata\":{\"geofence\":\"\",\"location\":{\"latitude\":39.913635,\"longitude\":116.496183},\"latitude\":39.913635,\"longitude\":116.496183},\"car_versions\":{\"version\":\"2024.45.32.7\",\"update_available\":false,\"update_version\":\"\"},\"driving_details\":{\"active_route\":{\"destination\":\"\",\"energy_at_arrival\":0,\"distance_to_arrival\":0,\"minutes_to_arrival\":0,\"traffic_minutes_delay\":0,\"location\":{\"latitude\":0,\"longitude\":0}},\"active_route_destination\":\"\",\"active_route_latitude\":0,\"active_route_longitude\":0,\"shift_state\":\"\",\"power\":0,\"speed\":0,\"heading\":265,\"elevation\":20},\"climate_details\":{\"is_climate_on\":false,\"inside_temp\":26,\"outside_temp\":24,\"is_preconditioning\":false,\"climate_keeper_mode\":\"off\"},\"battery_details\":{\"est_battery_range\":192.94,\"rated_battery_range\":170.99,\"ideal_battery_range\":170.99,\"battery_level\":34,\"usable_battery_level\":34},\"charging_details\":{\"plugged_in\":false,\"charging_state\":\"disconnected\",\"charge_energy_added\":43.82,\"charge_limit_soc\":90,\"charge_port_door_open\":false,\"charger_actual_current\":0,\"charger_phases\":0,\"charger_power\":0,\"charger_voltage\":1,\"charge_current_request\":16,\"charge_current_request_max\":16,\"scheduled_charging_start_time\":\"0001-01-01T08:05:43+08:05\",\"time_to_full_charge\":0},\"tpms_details\":{\"tpms_pressure_fl\":2.75,\"tpms_pressure_fr\":2.775,\"tpms_pressure_rl\":2.8,\"tpms_pressure_rr\":2.75,\"tpms_soft_warning_fl\":false,\"tpms_soft_warning_fr\":false,\"tpms_soft_warning_rl\":false,\"tpms_soft_warning_rr\":false}},\"units\":{\"unit_of_length\":\"km\",\"unit_of_pressure\":\"bar\",\"unit_of_temperature\":\"C\"}}}";
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

//        log.info("{},{}", latitude, longitude);

//        log.info(JSONUtil.toJsonStr(json));
        return JSONUtil.toJsonStr(json);
    }

    @ResponseBody
    @GetMapping(value = {"/api/v1/cars/1/drives/{id}"})
    public String drives(HttpServletRequest request, @PathVariable Integer id) {
        log.info("id:{}", id);
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
