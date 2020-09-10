package com.example.springforum.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.example.springforum.model.AppUserDetails;

@Repository
public class LoginUserRepository {

	@Autowired
	private JdbcTemplate jdbc;

	@Autowired
	private MessageSource messageSource;

	/** メッセージのキー(認証失敗) */
    private static final String BAD_CREDENTIALS = "AbstractUserDetailsAuthenticationProvider.badCredentials";

	// ユーザー情報を取得するSQL文
	private static final String USER_SQL = "SELECT * FROM m_user WHERE user_id = ?";

	// ユーザーのロールを取得するSQL文
	private static final String ROLE_SQL = "SELECT role FROM m_user WHERE user_id = ?";

	// ユーザー情報を取得してUserDetailsを生成するメソッド
	public UserDetails select(String userId) {

		AppUserDetails user = null;

		try {
			// ユーザーの取得
			Map<String, Object> userMap = jdbc.queryForMap(USER_SQL, userId);

			// 権限リストの取得
			List<GrantedAuthority> grantedAuthorityList = getRoleList(userId);

			// 結果返却用のUserDetailsを生成
			user = buildUserDetails(userMap, grantedAuthorityList);

		} catch(EmptyResultDataAccessException e) {
			// エラーメッセージ取得
			String message = messageSource.getMessage(BAD_CREDENTIALS,
				null,
				Locale.getDefault());

			// 例外を投げる
			throw new UsernameNotFoundException(message, e);
		}

		return user;
	}

	private List<GrantedAuthority> getRoleList(String userId) {

		// ユーザー権限の取得
		List<Map<String, Object>> authorityList = jdbc.queryForList(ROLE_SQL, userId);

		// 結果返却用のList生成
		List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();

		for(Map<String, Object> map: authorityList) {

			// ロール名を取得
			String roleName = (String)map.get("role");

			// SimpleGrantedAuthorityインスタンスの生成
			GrantedAuthority authority = new SimpleGrantedAuthority(roleName);

			grantedAuthorityList.add(authority);
		}

		return grantedAuthorityList;
	}

	// ユーザークラスの生成
	private AppUserDetails buildUserDetails(Map<String, Object> userMap,
								List<GrantedAuthority> grantedAuthorityList) {

		// Mapから値を取得
		String userId = (String)userMap.get("user_id");
		String password = (String)userMap.get("password");
		String appUserName = (String)userMap.get("user_name");

		// 結果返却用のUserDetaisを生成
		AppUserDetails user = AppUserDetails.builder()
				.userId(userId)
				.password(password)
				.appUserName(appUserName)
				.authority(grantedAuthorityList)
				.build();

		return user;
	}
}
