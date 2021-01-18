/*
 * Copyright (c) 2021 Henry 李恒 (henry.box@outlook.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pub.ihub.secure.oauth2.server.token;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken2;
import org.springframework.util.Assert;
import pub.ihub.core.ObjectBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static pub.ihub.core.IHubLibsVersion.SERIAL_VERSION_UID;

/**
 * OAuth 2.0令牌的容器。
 *
 * @author henry
 */
@EqualsAndHashCode
public class OAuth2Tokens implements Serializable {

	private static final long serialVersionUID = SERIAL_VERSION_UID;
	private final Map<Class<? extends AbstractOAuth2Token>, OAuth2TokenHolder> tokens;

	public OAuth2Tokens() {
		this.tokens = new HashMap<>();
	}

	public OAuth2Tokens(Map<Class<? extends AbstractOAuth2Token>, OAuth2TokenHolder> tokens) {
		this.tokens = new HashMap<>(tokens);
	}

	@Nullable
	public OAuth2AccessToken getAccessToken() {
		return getToken(OAuth2AccessToken.class);
	}

	@Nullable
	public OAuth2RefreshToken getRefreshToken() {
		OAuth2RefreshToken refreshToken = getToken(OAuth2RefreshToken.class);
		return refreshToken != null ? refreshToken : getToken(OAuth2RefreshToken2.class);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public <T extends AbstractOAuth2Token> T getToken(Class<T> tokenType) {
		Assert.notNull(tokenType, "tokenType cannot be null");
		OAuth2TokenHolder tokenHolder = this.tokens.get(tokenType);
		return tokenHolder != null ? (T) tokenHolder.getToken() : null;
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public <T extends AbstractOAuth2Token> T getToken(String token) {
		Assert.hasText(token, "token cannot be empty");
		OAuth2TokenHolder tokenHolder = this.tokens.values().stream()
			.filter(holder -> holder.getToken().getTokenValue().equals(token))
			.findFirst()
			.orElse(null);
		return tokenHolder != null ? (T) tokenHolder.getToken() : null;
	}

	@Nullable
	public <T extends AbstractOAuth2Token> OAuth2TokenMetadata getTokenMetadata(T token) {
		Assert.notNull(token, "token cannot be null");
		OAuth2TokenHolder tokenHolder = this.tokens.get(token.getClass());
		return (tokenHolder != null && tokenHolder.getToken().equals(token)) ? tokenHolder.getTokenMetadata() : null;
	}

	public void addToken(AbstractOAuth2Token token, OAuth2TokenMetadata tokenMetadata) {
		Assert.notNull(token, "token cannot be null");
		if (tokenMetadata == null) {
			tokenMetadata = ObjectBuilder.builder(OAuth2TokenMetadata::new).build();
		}
		tokens.put(token.getClass(), new OAuth2TokenHolder(token, tokenMetadata));
	}

	public void addToken(AbstractOAuth2Token token) {
		addToken(token, null);
	}

	// TODO 继续。。。
	public static Builder from(OAuth2Tokens tokens) {
		Assert.notNull(tokens, "tokens cannot be null");
		return new Builder(tokens.tokens);
	}

	public static class Builder implements Serializable {

		private static final long serialVersionUID = SERIAL_VERSION_UID;
		private Map<Class<? extends AbstractOAuth2Token>, OAuth2TokenHolder> tokens;

		protected Builder() {
			this.tokens = new HashMap<>();
		}

		protected Builder(Map<Class<? extends AbstractOAuth2Token>, OAuth2TokenHolder> tokens) {
			this.tokens = new HashMap<>(tokens);
		}

		public Builder accessToken(OAuth2AccessToken accessToken) {
			return addToken(accessToken, null);
		}

		public Builder accessToken(OAuth2AccessToken accessToken, OAuth2TokenMetadata tokenMetadata) {
			return addToken(accessToken, tokenMetadata);
		}

		public Builder refreshToken(OAuth2RefreshToken refreshToken) {
			return addToken(refreshToken, null);
		}

		public Builder refreshToken(OAuth2RefreshToken refreshToken, OAuth2TokenMetadata tokenMetadata) {
			return addToken(refreshToken, tokenMetadata);
		}

		public <T extends AbstractOAuth2Token> Builder token(T token) {
			return addToken(token, null);
		}

		public <T extends AbstractOAuth2Token> Builder token(T token, OAuth2TokenMetadata tokenMetadata) {
			return addToken(token, tokenMetadata);
		}

		protected Builder addToken(AbstractOAuth2Token token, OAuth2TokenMetadata tokenMetadata) {
			Assert.notNull(token, "token cannot be null");
			if (tokenMetadata == null) {
				tokenMetadata = ObjectBuilder.builder(OAuth2TokenMetadata::new).build();
			}
			this.tokens.put(token.getClass(), new OAuth2TokenHolder(token, tokenMetadata));
			return this;
		}

		public OAuth2Tokens build() {
			return new OAuth2Tokens(this.tokens);
		}
	}

	@RequiredArgsConstructor
	@Getter
	@EqualsAndHashCode
	protected static class OAuth2TokenHolder implements Serializable {

		private static final long serialVersionUID = SERIAL_VERSION_UID;
		private final AbstractOAuth2Token token;
		private final OAuth2TokenMetadata tokenMetadata;

	}
}
