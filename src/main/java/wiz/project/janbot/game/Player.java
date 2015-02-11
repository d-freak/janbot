/**
 * Player.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;

import java.io.Serializable;
import java.util.Objects;



/**
 * プレイヤー
 */
final class Player implements Serializable {
    
    /**
     * コンストラクタ
     */
    public Player() {
    }
    
    /**
     * コンストラクタ
     * 
     * @param name 名前。
     * @param type タイプ。
     */
    public Player(final String name, final PlayerType type) {
        setName(name);
        setType(type);
    }
    
    /**
     * コピーコンストラクタ
     * 
     * @param source 複製元。
     */
    public Player(final Player source) {
        if (source != null) {
            _name = source._name;
            _type = source._type;
        }
    }
    
    
    
    /**
     * 等価なオブジェクトか
     * 
     * @param target 比較対象。
     * @return 比較結果。
     */
    @Override
    public boolean equals(final Object target) {
        if (target == null) {
            return false;
        }
        if (this == target) {
            return true;
        }
        if (!(target instanceof Player)) {
            return false;
        }
        
        final Player targetPlayer = (Player)target;
        return _name.equals(targetPlayer._name) &&
               _type.equals(targetPlayer._type);
    }
    
    /**
     * 名前を取得
     * 
     * @return 名前。
     */
    public String getName() {
        return _name;
    }
    
    /**
     * タイプを取得
     * 
     * @return タイプ。
     */
    public PlayerType getType() {
        return _type;
    }
    
    /**
     * ハッシュコードを取得
     * 
     * @return ハッシュコード。
     */
    @Override
    public int hashCode() {
        return Objects.hash(_name, _type);
    }
    
    /**
     * 文字列に変換
     * 
     * @return 変換結果。
     */
    @Override
    public String toString() {
        return _name;
    }
    
    
    
    /**
     * 名前を設定
     * 
     * @param name 名前。
     */
    private void setName(final String name) {
        if (name != null) {
            _name = name;
        }
        else {
            _name = "";
        }
    }
    
    /**
     * タイプを設定
     * 
     * @param type タイプ。
     */
    private void setType(final PlayerType type) {
        if (type != null) {
            _type = type;
        }
        else {
            _type = PlayerType.COM;
        }
    }
    
    
    
    /**
     * シリアルバージョン
     */
    private static final long serialVersionUID = 1L;
    
    
    
    /**
     * 名前
     */
    private String _name = "";
    
    /**
     * タイプ
     */
    private PlayerType _type = PlayerType.COM;
    
}

