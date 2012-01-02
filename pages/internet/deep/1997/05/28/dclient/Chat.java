//
// Chat - a simple applet with a WhoPanel and ChatPanel
//
// This code is Copyright (C) 1997, go2net Inc. Permission is granted for
// any use so long as this header remains intact.
//
// Originally published in Deep Magic:
//     <URL:http://www.go2net.com/internet/deep/>
//
// The code herein is provided to you as is, without any warranty of any
// kind, including express or implied warranties, the warranties of
// merchantability and fitness for a particular purpose, and
// non-infringement of proprietary rights.  The risk of using this code
// remains with you.

package dclient;

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

        add("East", _whopanel);
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
                    if (_whopanel.setNickname(nickname + "." + i))  {
                        _client.setNickname(nickname);
                        break;
                    }
                }
            }

            _chatpanel.setClient(_client, "chat.object");

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
    ChatPanel _chatpanel = new ChatPanel(400);
    WhoPanel _whopanel = new WhoPanel();
}
