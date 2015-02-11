/**
 * JanBOT.java
 * 
 * @Author Yuki
 */

package wiz.project.janbot;

import wiz.project.ircbot.IRCBOT;




/**
 * BOT本体
 */
public final class JanBOT {
    
    /**
     * コンストラクタを自分自身に限定許可
     */
    private JanBOT() {
    }
    
    
    
    /**
     * エントリポイント
     * 
     * @param paramList 実行引数リスト。
     */
    public static void main(final String[] paramList) {
        if (paramList == null) {
            throw new NullPointerException("Parameter list is null.");
        }
        if (paramList.length < PARAM_SIZE) {
            System.out.println("Call with parameter. (ex.: java -jar janbot.jar \"foo,irc.net\" \"1234\" \"#your-channel\")");
            return;
        }
        
        try {
            final String serverHost = paramList[PARAM_INDEX_SERVER_URI];
            final int serverPort = Integer.parseInt(paramList[PARAM_INDEX_SERVER_PORT]);
            final String channel = paramList[PARAM_INDEX_CHANNEL_NAME];
            IRCBOT.getInstance().initialize(BOT_NAME, serverHost, serverPort, channel, new MessageListener<>());
        }
        catch (final Throwable e) {
            e.printStackTrace();
        }
    }
    
    
    
    /**
     * BOTのニックネーム
     */
    private static final String BOT_NAME = "JanBOT";
    
    /**
     * 実行パラメータサイズ
     */
    private static final int PARAM_SIZE = 3;
    
    /**
     * 実行パラメータインデックス
     */
    private static final int PARAM_INDEX_SERVER_URI   = 0;
    private static final int PARAM_INDEX_SERVER_PORT  = 1;
    private static final int PARAM_INDEX_CHANNEL_NAME = 2;
    
}

