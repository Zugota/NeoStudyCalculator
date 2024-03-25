package com.example.NeostudyCalculator.controller;


import com.example.NeostudyCalculator.models.Day;
import de.jollyday.HolidayManager;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MainController {

    @GetMapping("/")
    public String greeting(Model model) {
        return "index";
    }

    @GetMapping("/calculate")
    public String calcul(@RequestParam String salary, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateStart,
                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateEnd,  Model model) {
        long totalDays = ChronoUnit.DAYS.between(dateStart.toInstant(), dateEnd.toInstant()) + 1;

        List<Day> days = new ArrayList<>();
        days = parseCalendar();

        List<Day> selectedDays = getCurrDays(dateStart, dateEnd, days);

        short countOfHolidays = 0;

        for (Day day : selectedDays) {
            if (!day.isWorkday()) countOfHolidays++;
        }

        totalDays -= countOfHolidays;

        Double sal = Double.parseDouble(salary);
        Double counted = (sal / 365.0) * totalDays;

        model.addAttribute("counted", round(counted,2));
        return "calculated";
    }

    public List<Day> parseCalendar() {
        List<Day> days = new ArrayList<>();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse("src/main/resources/static/xml/calendar2024.xml");
            doc.getDocumentElement().normalize();
            NodeList zapList = doc.getElementsByTagName("ZAP");
            for (int temp = 0; temp < zapList.getLength(); temp++) {
                Element zapElement = (Element) zapList.item(temp);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(zapElement.getElementsByTagName("DATE").item(0).getTextContent());
                Integer wday = Integer.parseInt(zapElement.getElementsByTagName("WORKDAY").item(0).getTextContent());
                boolean ifWorkday = wday != 0;
                try {

                    days.add(new Day(date, ifWorkday, zapElement.getElementsByTagName("DAY_TYPE").item(0).getTextContent(), zapElement.getElementsByTagName("REASON").item(0).getTextContent()));
                } catch (java.lang.NullPointerException exc) {

                    days.add(new Day(date, ifWorkday, zapElement.getElementsByTagName("DAY_TYPE").item(0).getTextContent(), ""));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return days;
    }

    public List<Day> getCurrDays(Date dateStart, Date dateEnd, List<Day> days) {
        List<Day> selectedDays = new ArrayList();
        for (Day day : days) {
            if (day.getDate().compareTo(dateStart) >= 0 && day.getDate().compareTo(dateEnd) <= 0) {
                selectedDays.add(day);
            }
        }
        return selectedDays;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}