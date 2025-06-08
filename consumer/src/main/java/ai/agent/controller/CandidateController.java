package ai.agent.controller;

import ai.agent.services.CandidatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/candidate")
@RestController
public class CandidateController {
    // @Autowired
    private CandidatesService candidatesService;

    public CandidateController(CandidatesService candidatesService) {
        this.candidatesService = candidatesService;
    }
}
