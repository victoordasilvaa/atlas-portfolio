package dev.portfolio.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.portfolio.domain.BusinessException;
import dev.portfolio.dto.MemberDto;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class HttpMemberDirectory implements MemberDirectory {
  private final String base, authorization;
  private final ObjectMapper json = new ObjectMapper();

  public HttpMemberDirectory(String base, String username, String password) {
    this.base = base.replaceAll("/$", "");
    this.authorization =
        "Basic "
            + Base64.getEncoder()
                .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
  }

  public MemberDto get(long id) {
    try {
      return json.readValue(read("/members/" + id), MemberDto.class);
    } catch (IOException e) {
      throw unavailable();
    }
  }

  public List<MemberDto> list() {
    try {
      return json.readValue(read("/members"), new TypeReference<List<MemberDto>>() {});
    } catch (IOException e) {
      throw unavailable();
    }
  }

  private byte[] read(String path) {
    HttpURLConnection c = null;
    try {
      c = (HttpURLConnection) new URL(base + path).openConnection();
      c.setInstanceFollowRedirects(false);
      c.setConnectTimeout(2000);
      c.setReadTimeout(3000);
      c.setRequestProperty("Authorization", authorization);
      c.setRequestProperty("Accept", "application/json");
      int status = c.getResponseCode();
      if (status == 404)
        throw new BusinessException(
            "MEMBER_NOT_FOUND", "Membro não encontrado no diretório externo.", 422);
      if (status != 200) throw unavailable();
      try (InputStream in = c.getInputStream();
          ByteArrayOutputStream out = new ByteArrayOutputStream()) {
        byte[] buffer = new byte[4096];
        int n;
        while ((n = in.read(buffer)) != -1) {
          if (out.size() + n > 1048576) throw unavailable();
          out.write(buffer, 0, n);
        }
        return out.toByteArray();
      }
    } catch (IOException e) {
      throw unavailable();
    } finally {
      if (c != null) c.disconnect();
    }
  }

  private BusinessException unavailable() {
    return new BusinessException(
        "DIRECTORY_UNAVAILABLE", "Serviço de membros indisponível. Tente novamente.", 503);
  }
}
