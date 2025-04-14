package Email;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SendMail {

    public static void main(String[] args) {
        List<String> emailIds = new ArrayList();

        Executor executor = new Executor();
        EmailService emailService = new EmailService();
        Integer totalRetiresAvailable = 3;

        List<String> failedMails = emailIds;
        List<String> successMails = new ArrayList<>();

        for (int i = 0; i < totalRetiresAvailable; ++i) {
            List<String> emailsToWorkOn = failedMails;
            failedMails.clear();
            List<CompletableFuture> completableFutures = new ArrayList<>();
            emailsToWorkOn.forEach(
                    (emailId) -> {
                        completableFutures.add(
                                CompletableFuture.supplyAsync(() -> {
                                    boolean response = emailService.sendMail(emailId);
                                    if (response) {
                                        successMails.add(emailId);
                                    }
                                    else{
                                        failedMails.add(emailId);
                                    }
                                    return response;
                                }, executor.executor)
                        );
                    }
            );
            for (CompletableFuture completableFuture : completableFutures) {
                completableFuture.join();
            }
            if(failedMails.isEmpty()){
                break;
            }
        }

    }

}
