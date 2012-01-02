//
// Rendevous - the main applet that allows chatting and game creation
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

public class Rendevous extends Applet
{
    //
    // Rendevous public member functions

    public void init ()
    {
        setLayout(new BorderLayout(3, 0));
        add("West", _whop);
        add("Center", _chatp);
        add("East", _gamesp);
    }

    public void start ()
    {
        try {
            _client = new Client(getDocumentBase().getHost());
            _whop.setClient(_client);
            _gamesp.setClient(_client);

            String nickname = InetAddress.getLocalHost().getHostName();
            if (!_whop.setNickname(nickname)) {
                for (int i = 0; i < 256; i++) {
                    if (_whop.setNickname(nickname + "." + i)) break;
                }
            }

            _chatp.setClient(_client, "chat.object");

        } catch (IOException e) {
            System.err.println("Network error: " + e);
        }
    }

    public void stop ()
    {
        if (_client != null) _client.disconnect();
        remove(_chatp);
        remove(_whop);
        remove(_gamesp);
    }

    //
    // Rendevous protected data members

    Client _client;
    WhoPanel _whop = new WhoPanel();
    ChatPanel _chatp = new ChatPanel(80);
    GamesPanel _gamesp = new GamesPanel();
}
