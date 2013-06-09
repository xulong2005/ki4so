package com.github.ebnew.ki4so.core.authentication;

/**
 * 认证处理器类，该类检查用户的凭证是否合法。
 * @author Administrator
 * @version 1.0
 * @created 08-六月-2013 9:56:10
 */
public interface AuthenticationHandler {

	/**
	 * 认证方法，返回true表示认证成功，false表示认证失败
	 * 
	 * @param credential
	 */
	public boolean authenticate(Credential credential);

	/**
	 * 是否支持用户凭证credential的认证处理，返回值true表示支持，
	 * false表示不支持，若不支持该凭证，则忽略。
	 * 
	 * @param credential
	 */
	public boolean supports(Credential credential);

}