//
// Chat

package chat;

import dclient.Client;

import java.applet.*;
import java.awt.BorderLayout;
import java.io.IOException;
import java.net.InetAddress;

public class Chat extends Applet
{
    //
    // Chat public member functions

    public void init ()
    {
        setLayout(new BorderLayout());

        _whopanel = new WhoPanel();
        add("East", _whopanel);

        _chatpanel = new ChatPanel(_whopanel);
        add("Center", _chatpanel);
    }

    public void start ()
    {
        try {
            _client = new Client(getDocumentBase().getHost());
            _whopanel.setClient(_client);

            String nickname = InetAddress.getLocalHost().getHostName();
            if (!_whopanel.setNickname(nickname)) {
                for (int i = 0; i < 256; i++) {
                    if (_whopanel.setNickname(nickname + "." + i)) break;
                }
            }

            _chatpanel.setClient(_client);

        } catch (IOException e) {
            System.err.println("Network error: " + e);
        }
    }

    public void stop ()
    {
        _client.disconnect();
        remove(_chatpanel);
        remove(_whopanel);
    }

    //
    // Chat protected data members

    Client _client;
    ChatPanel _chatpanel;
    WhoPanel _whopanel;
}
