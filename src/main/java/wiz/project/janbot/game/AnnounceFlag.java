/**
 * AnnounceFlag.java
 * 
 * @author Yuki
 */

package wiz.project.janbot.game;



/**
 * 実況フラグ
 */
public enum AnnounceFlag {
    
    /**
     * ゲーム開始
     */
    GAME_START,
    
    /**
     * ランキング
     */
    RANKING,
    
    /**
     * ロン和了
     */
    COMPLETE_RON,
    
    /**
     * ツモ和了
     */
    COMPLETE_TSUMO,
    
    /**
     * 流局
     */
    GAME_OVER,
    
    /**
     * ゲーム終了
     */
    GAME_END,
    
    /**
     * ロン可能
     */
    CALLABLE_RON,
    
    /**
     * チー可能
     */
    CALLABLE_CHI,
    
    /**
     * ポン可能
     */
    CALLABLE_PON,
    
    /**
     * 大明カン可能
     */
    CALLABLE_KAN,
    
    /**
     * 手牌
     */
    HAND,
    
    /**
     * ツモ牌
     */
    ACTIVE_TSUMO,
    
    /**
     * 直前の捨て牌
     */
    ACTIVE_DISCARD,
    
    /**
     * 副露直後か
     */
    AFTER_CALL,
    
    /**
     * 場情報
     */
    FIELD,
    
    /**
     * 捨て牌情報
     */
    RIVER_SINGLE,
    
    /**
     * 全捨て牌情報
     */
    RIVER_ALL,
    
    /**
     * 裏ドラ
     */
    URA_DORA,
    
    /**
     * 監視モード開始
     */
    WATCHING_START,
    
    /**
     * 監視モード終了
     */
    WATCHING_END,
    
    /**
     * 指定牌の残り枚数
     */
    OUTS,
    
    /**
     * 七対モード切り替え
     */
    SEVENTH,
    
    /**
     * 確認メッセージ用
     */
    CONFIRM,
    
    /**
     * 点数
     */
    SCORE,
    
    /**
     * 8点縛り超え
     */
    OVER_TIED_POINT,
    
    /**
     * 8点縛り超えず
     */
    NOT_OVER_TIED_POINT,
    
    /**
     * 待ち牌変更
     */
    CHANGE_WAIT;
    
    
    
    /**
     * 副露可能か
     * 
     * @return 判定結果。
     */
    public boolean isCallable() {
        switch (this) {
        case CALLABLE_RON:
        case CALLABLE_CHI:
        case CALLABLE_PON:
        case CALLABLE_KAN:
            return true;
        default:
            return false;
        }
    }
    
}

