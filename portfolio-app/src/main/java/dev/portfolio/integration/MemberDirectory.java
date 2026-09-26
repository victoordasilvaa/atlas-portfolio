package dev.portfolio.integration;

import dev.portfolio.dto.MemberDto;
import java.util.List;

public interface MemberDirectory {
  MemberDto get(long id);

  List<MemberDto> list();
}
