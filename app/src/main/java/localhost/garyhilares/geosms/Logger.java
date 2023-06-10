package localhost.garyhilares.geosms;

import android.widget.TextView;
import java.util.Arrays;

public class Logger {
    final int MAX_MESSAGES = 20;
    TextView view;
    int messageCount = 0;
    String messages[] = new String[MAX_MESSAGES];

    public Logger(TextView newView) {
        view = newView;
    }

    public void addMessage(String message) {
        if (messageCount < MAX_MESSAGES) {
            messages[messageCount] = message;
            messageCount++;
        } else {
            for (int i = 0; i < MAX_MESSAGES - 1; i++) {
                messages[i] = messages[i + 1];
            }
            messages[MAX_MESSAGES - 1] = message;
        }
        view.setText(String.join("\n",
                messageCount > MAX_MESSAGES ? messages : Arrays.copyOfRange(messages, 0, messageCount)));
    }
}