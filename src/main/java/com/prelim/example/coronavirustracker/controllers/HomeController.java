package com.prelim.example.coronavirustracker.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prelim.example.coronavirustracker.models.LocationStats;
import com.prelim.example.coronavirustracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {
    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public  String home(Model model){
        List<LocationStats> allStats=coronaVirusDataService.getAllstats();
        Map<String, Integer> totalCaseGroupedbyCountry =
                allStats.stream().collect(Collectors.groupingBy(LocationStats::getCountry,
                        Collectors.summingInt(LocationStats::getLatestTotalCases)));
        int totalReportedCases=allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalNewCases=allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();

        ObjectMapper objectMapper = new ObjectMapper();
        String strTotalCaseGroupedbyCountry = null;
        try {
            strTotalCaseGroupedbyCountry = objectMapper.writeValueAsString(totalCaseGroupedbyCountry);
        } catch (JsonProcessingException e) {
            strTotalCaseGroupedbyCountry = "";
        }
        model.addAttribute("locationStats", allStats);
        model.addAttribute("totalCaseGroupedbyCountry", strTotalCaseGroupedbyCountry);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);
        return "home";
    }
}
