package app.wishlist.model.domain.event;

import app.wishlist.model.domain.user.User;
import app.wishlist.model.report.ReportInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SecretSantaSatisfactionQuestionnaire implements ReportInterface {

    private final User user;
    private int rating;
    private String comments;
    private boolean wouldParticipateAgain;

    @Override
    public String getReportContent() {
        return String.format("""
                        User: %s (@%s)
                        Rating: %d / 5
                        Would Participate Again: %s
                        
                        Comments:
                        %s
                        """,
                user.getFullName(), user.getLogin(),
                rating,
                wouldParticipateAgain ? "YES" : "NO",
                comments);
    }

    @Override
    public String getAuthor() {
        return user.getLogin();
    }
}
