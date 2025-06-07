package ai.agent.util;

import ai.agent.data.Resume;
import ai.agent.record.CandidateRecord;
import ai.agent.record.InterViewRecord;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 姚东名
 * Date: 2025-06-07
 * Time: 20:31
 */
public class Dto2Record {
    public static InterViewRecord toInterViewDetails(Resume resume){
        return new InterViewRecord(
                resume.getId().toString(),
                resume.getName(),
                resume.getScore(),
                resume.getInterViewStatus().toString(),
                resume.getEvaluate(),
                resume.getEmail(),
                resume.getMp3Path(),
                resume.getInterviewEvaluate()
        );
    }

    /*public static CandidateRecord toCandidateRecord( candidates) {
        return new CandidateRecord(

        )
    }*/
}
