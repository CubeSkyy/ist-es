package pt.ulisboa.tecnico.softeng.car.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pt.ulisboa.tecnico.softeng.car.services.local.RentACarInterface;
import pt.ulisboa.tecnico.softeng.car.services.local.dataobjects.RentingData;

import java.util.LinkedList;
import java.util.List;


@Controller
@RequestMapping(value = "/rentacars/rentacar/{rentACarcode}/processor")
public class ProcessorController {
    private static final Logger logger = LoggerFactory.getLogger(ProcessorController.class);

    @RequestMapping(method = RequestMethod.GET)
    public String processorForm(Model model, @PathVariable String rentACarcode) {
        logger.info("processorForm rentACarcode{}", rentACarcode);

        List<RentingData>  rents = (new RentACarInterface()).getProcessorsRentsByRentACarCode(rentACarcode);

        model.addAttribute("rents", rents);
        model.addAttribute("rentACarcode", rentACarcode);
        model.addAttribute("numRents", rents.size());


        return "pendingRentsView";
    }
}
