package com.PTUT.WebDysgraphie.controllers;

import com.PTUT.WebDysgraphie.models.Point;
import com.PTUT.WebDysgraphie.models.Tableau;
import com.PTUT.WebDysgraphie.models.Trace;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
public class DysController {

    private ArrayList<Point> listPoint = new ArrayList<Point>(); // Liste des points à tracer.
    private ArrayList<Double> listPression = new ArrayList<Double>();
    private Tableau tableau;
    private long tempsDebut; // Temps de debut
    private long tempsPrecedent;
    private String sexe;
    private String niveau;
    private String time;


    @RequestMapping("/init")
    public String init(){
        this.tempsDebut = System.currentTimeMillis();
        return "page";
    }

    @RequestMapping("/write")
    public String write(Model model) {
        model.addAttribute("distance","");
        model.addAttribute("pression","");
        model.addAttribute("acceleration","");
        model.addAttribute("crayon","");
        this.listPoint.clear();
        return "page";
    }

    @RequestMapping("/infos")
    public String infos(){ return "infos"; }

    @GetMapping("/results")
    public String result(@RequestParam String timer, Model model) {
        Trace trace = new Trace(this.listPoint);
        model.addAttribute("distance","ok");
        model.addAttribute("pression","ko");
        model.addAttribute("acceleration","ok");
        model.addAttribute("crayon","ko");
        this.time = timer;
        return "page::#details";
    }

    @RequestMapping("/saveInfos")
    public String saveInfos(@RequestParam String sexe, @RequestParam String niveau){
        this.sexe = sexe;
        this.niveau = niveau;
        return "show";
    }

    @PostMapping("/addPoint")
    public String add(@RequestParam int pointX, @RequestParam int pointY) {
        long time = System.currentTimeMillis() - this.tempsDebut;
        long intervalle = 0;
        if(this.listPoint.size()!=0) intervalle = time - this.tempsPrecedent;
        Point point = new Point(pointX,pointY,this.listPoint.size(),(int)intervalle,(int)time);
        this.listPoint.add(point);
        this.tempsPrecedent = time;
        return "page";
    }

    @PostMapping("/addPression")
    public String addPressure(@RequestParam double pression){
        this.listPression.add(pression);
        return "page";
    }

    @RequestMapping("/download")
    public String download() {
        this.tableau = new Tableau("fichier-"+System.currentTimeMillis()+".csv", "sheet1", this.listPoint, this.listPression,this.sexe,this.niveau);
        return "page";
    }


}
