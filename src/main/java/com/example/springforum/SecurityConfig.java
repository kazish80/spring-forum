package com.example.springforum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	@Qualifier("UserDetailsServiceImpl")
	private UserDetailsService userDetailsService;

	// パスワードエンコーダーのBean定義
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/* 独自ユーザークラス使用のため削除
	// データソース
	@Autowired
	private DataSource dataSource;

	// ユーザーIDとパスワードを取得するSQL文
	private static final String USER_SQL
		= "SELECT user_id, password, true FROM m_user WHERE user_id = ?";

	// ユーザーのロールを取得するSQL文
	private static final String ROLE_SQL
		= "SELECT user_id, role FROM m_user WHERE user_id = ?";
	*/

	@Override
	public void configure(WebSecurity web) throws Exception {

		//静的リソースへのアクセスには、セキュリティを適用しない
		web.ignoring().antMatchers("/webjars/**", "/css/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		//ログイン不要ページの設定
		http.authorizeRequests()
			.antMatchers("/webjars/**").permitAll()	// webjarsへのアクセス許可
			.antMatchers("/css/**").permitAll()		// cssへのアクセス許可
			.antMatchers("/").permitAll()			// ホーム画面は直リンクOK
			.antMatchers("/login").permitAll()
			.antMatchers("/signup").permitAll()		// ユーザー登録はログイン不要
			.antMatchers("/post/new").authenticated()		// 質問投稿はログインが必要
			.antMatchers("/post/**/reply").authenticated()	// 回答投稿はログインが必要
			.antMatchers("/post/**/edit").authenticated()	// 質問編集はログインが必要
			.antMatchers("/post/**/reply/**/edit").authenticated()	// 回答編集はログインが必要
			.antMatchers("/post/**").permitAll()		// 質問閲覧はログイン不要
			.antMatchers("/search**").permitAll()		// 検索はログイン不要
			.antMatchers("/appinfo").permitAll()		// アプリケーション情報はログイン不要
			.anyRequest().authenticated();			// それ以外は直リンク禁止

		// ログイン処理
		http.formLogin()
			.loginProcessingUrl("/login")		// ログイン処理後のパス (ログイン画面の formの action="xxx" に一致)
			.loginPage("/login")				// ログインページの指定
			.failureUrl("/login?error=1")		// ログイン失敗時の遷移先
			.usernameParameter("userId")		// ログインページのユーザーID
			.passwordParameter("password")		// ログインページのパスワード
			.defaultSuccessUrl("/", false);		// ログイン成功後の遷移先

		// ログアウト処理
		http.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")) //GETメソッドでログアウトする場合
			.logoutUrl("/logout")		// POSTメソッドでログアウトする場合 (デフォルト)
			.logoutSuccessUrl("/");		// ログアウト成功時の遷移先

		// CSRF対策を無効に設定 (一時的)
		//http.csrf().disable();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		/* 独自ユーザークラス使用のため削除
		// ログイン処理時のユーザー情報を、DBから取得する
		auth.jdbcAuthentication()
			.dataSource(dataSource)
			.usersByUsernameQuery(USER_SQL)
			.authoritiesByUsernameQuery(ROLE_SQL)
			.passwordEncoder(passwordEncoder());
		*/

		auth.userDetailsService(userDetailsService)
			.passwordEncoder(passwordEncoder());
	}
}
