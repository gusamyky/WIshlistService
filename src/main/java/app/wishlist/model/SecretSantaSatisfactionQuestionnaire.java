package app.wishlist.model;

public class SecretSantaSatisfactionQuestionnaire implements ReportInterface {

    private final User user;
    private int rating; // 1 to 5
    private String comments;
    private boolean wouldParticipateAgain;

    public SecretSantaSatisfactionQuestionnaire(User user, int rating, String comments, boolean again) {
        this.user = user;
        this.rating = rating;
        this.comments = comments;
        this.wouldParticipateAgain = again;
    }

    @Override
    public String getReportTitle() {
        return "Satisfaction Report: " + user.getFullName();
    }

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
                comments
        );
    }

    @Override
    public String getAuthor() {
        return user.getLogin();
    }
}
