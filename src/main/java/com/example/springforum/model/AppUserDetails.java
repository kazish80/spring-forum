package com.example.springforum.model;

import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUserDetails implements UserDetails {

	// =========================
    // Springで必要なフィールド
    // =========================
    private String userId; //ユーザーID
    private String password; //パスワード
    private Date passUpdateDate; //パスワード更新日付
    private int loginMissTimes; //ログイン失敗回数
    private boolean unlock; //ロック状態フラグ
    //private boolean enabled; //有効・無効フラグ // 使ってないよと怒られるのでコメントアウトしておく
    private Date userDueDate; //ユーザー有効期限
    //権限のCollection
    private Collection<? extends GrantedAuthority> authority;

    // =========================
    // 独自のフィールド
    // =========================
    private String appUserName; //ユーザー名

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authority;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.userId;
	}

	/** アカウントの有効期限チェック
     * true: 有効 (期限切れでない)
     * false:無効 (期限切れ)
     */
	@Override
	public boolean isAccountNonExpired() {
		return true;	// ユーザー有効期限のフィールドは使用しない
	}

	/** アカウントのロックチェック
     * true: 有効
     * false: 無効
     */
	@Override
	public boolean isAccountNonLocked() {
		return true;	// ログイン失敗回数、ロック状態フラグのフィールドは使用しない
	}

	/** パスワードの有効期限チェック
     * true:有効
     * false:無効
     */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;	// パスワード更新日付のフィールドは使用しない
	}

	/** アカウントの有効・無効チェック
     * true:有効
     * false:無効
     */
	@Override
	public boolean isEnabled() {
		return true;	// 有効・無効フラグのフィールドは使用しない
	}

}
