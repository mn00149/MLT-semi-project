package com.study.member;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.study.message.FindIdOrPasswd;
import com.study.utility.Utility;

@Controller
public class MemberController {
  @Autowired
  @Qualifier("com.study.member.MemberServiceImpl")
  private MemberService service;

  @GetMapping
  public String home() {
    return "/home";
  }

  @GetMapping("/member/mypage")
  public String mypage(HttpSession session, Model model) {
    String id = (String) session.getAttribute("id");

    if (id == null) {
      return "redirect:/member/login/";
    } else {

      MemberDTO dto = service.mypage(id);

      model.addAttribute("dto", dto);

      return "/member/mypage";
    }
  }
  
  /*
   * @PostMapping("/member/mypage") public String updateFile(MultipartFile
   * filenameMF, String oldfile, String id) throws IOException { String basePath =
   * UploadMem.getUploadDir(); if (oldfile != null &&
   * !oldfile.equals("member.jpg")) { // 원본파일 삭제 Utility.deleteFile(basePath,
   * oldfile); } // pstorage에 변경 파일 저장 Map map = new HashMap(); map.put("id", id);
   * map.put("fname", Utility.saveFileSpring(filenameMF, basePath));
   * 
   * 
   * // 디비에 파일명 변경 int cnt = service.updateFile(map);
   * 
   * if (cnt == 1) { return "redirect:./mypage"; } else { return "error"; }
   * 
   * }
   */

  @RequestMapping("/admin/member/list")
  public String list(HttpServletRequest request) {
    // 검색관련------------------------
    String col = Utility.checkNull(request.getParameter("col"));
    String word = Utility.checkNull(request.getParameter("word"));

    if (col.equals("total")) {
      word = "";
    }

    // 페이지관련-----------------------
    int nowPage = 1;// 현재 보고있는 페이지
    if (request.getParameter("nowPage") != null) {
      nowPage = Integer.parseInt(request.getParameter("nowPage"));
    }
    int recordPerPage = 3;// 한페이지당 보여줄 레코드갯수

    // DB에서 가져올 순번(mysql)-----------------
    int sno = (nowPage - 1) * recordPerPage;
    int eno = recordPerPage;

    Map map = new HashMap();
    map.put("col", col);
    map.put("word", word);
    map.put("sno", sno);
    map.put("eno", eno);

    int total = service.total(map);

    List<MemberDTO> list = service.list(map);

    String paging = Utility.paging(total, nowPage, recordPerPage, col, word);

    // request에 Model사용 결과 담는다
    request.setAttribute("list", list);
    request.setAttribute("nowPage", nowPage);
    request.setAttribute("col", col);
    request.setAttribute("word", word);
    request.setAttribute("paging", paging);

    return "/member/list";

  }

  @GetMapping("/member/logout")
  public String logout(HttpSession session) {
    // 하나하나지울때
    // session.removeAttribute("id");
    // session.removeAttribute("grade");
    // session.removeAttribute("mname");
    
    //세션에 있던거 한번에 무효화
    session.invalidate();

    return "redirect:/";
  }

  @PostMapping("/member/login")
  public String login(@RequestParam Map<String, String> map, HttpSession session, HttpServletResponse response,
      HttpServletRequest request, Model model) {
    int cnt = service.loginCheck(map);

    if (cnt > 0) {// 회원이다.
      Map gmap = service.getGrade(map.get("id"));
      session.setAttribute("id", map.get("id"));
      session.setAttribute("grade", gmap.get("grade"));//세션객체에 저장하는중...//세선무효화하는 방법: 자동으로 되거나 로그아웃됏을때.
      session.setAttribute("mname", gmap.get("mname"));

      // Cookie 저장,id저장 여부 및 id
      Cookie cookie = null;
      String c_id = request.getParameter("c_id");
      if (c_id != null) {
        cookie = new Cookie("c_id", c_id); // c_id=> Y
        cookie.setMaxAge(60 * 60 * 24 * 365);// 1년
        response.addCookie(cookie);// 요청지(client:브라우저 컴) 쿠키 저장

        cookie = new Cookie("c_id_val", map.get("id"));
        cookie.setMaxAge(60 * 60 * 24 * 365);// 1년
        response.addCookie(cookie);// 요청지(client:브라우저 컴) 쿠키 저장
      } else {
        cookie = new Cookie("c_id", ""); // 쿠키 삭제
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        cookie = new Cookie("c_id_val", "");// 쿠키 삭제
        cookie.setMaxAge(0);
        response.addCookie(cookie);

      }

    } // ifcnt>0 end

    if (cnt > 0) {

      if (map.get("rurl") != null && !map.get("rurl").equals("")) {
        model.addAttribute("bbsno", map.get("bbsno"));
        model.addAttribute("nPage", map.get("nPage"));
        model.addAttribute("nowPage", map.get("nowPage"));
        model.addAttribute("col", map.get("col"));
        model.addAttribute("word", map.get("word"));

        return "redirect:" + map.get("rurl");
      } else {

        return "redirect:/";
      }

    } else {
      model.addAttribute("msg", "아이디 또는 비밀번호를 잘못 입력 했거나 <br>회원이 아닙니다. 회원가입 하세요");
      return "/member/errorMsg";
    }
  }

  @GetMapping("/member/login")
  public String login(HttpServletRequest request) {
    /*----쿠키설정 내용시작----------------------------*/
    String c_id = ""; // ID 저장 여부를 저장하는 변수, Y
    String c_id_val = ""; // ID 값

    Cookie[] cookies = request.getCookies();
    Cookie cookie = null;

    if (cookies != null) {
      for (int i = 0; i < cookies.length; i++) {
        cookie = cookies[i];

        if (cookie.getName().equals("c_id")) {
          c_id = cookie.getValue(); // Y (저장여부)
        } else if (cookie.getName().equals("c_id_val")) {
          c_id_val = cookie.getValue(); // user1...
        }
      }
    }
    /*----쿠키설정 내용 끝----------------------------*/

    request.setAttribute("c_id", c_id);
    request.setAttribute("c_id_val", c_id_val);

    return "/member/login";
  }

  @GetMapping("/member/agree")
  public String agree() {

    return "/member/agree";
  }

  @PostMapping("/member/createForm")
  public String create() {
    return "/member/create";
  }

  @PostMapping("/member/create")
  public String create(MemberDTO dto, HttpServletRequest request) throws IOException {
    String upDir = UploadMem.getUploadDir();
    String fname = Utility.saveFileSpring(dto.getFnameMF(), upDir);
    int size = (int) dto.getFnameMF().getSize();
    if (size > 0) {
      dto.setFname(fname);
    } else {
      dto.setFname("member.jpg");
    }

    if (service.create(dto) > 0) {
      return "redirect:/";
    } else {
      return "error";
    }
  }

  @GetMapping(value = "/member/emailcheck", produces = "application/json;charset=utf-8")
  @ResponseBody
  public Map<String, String> emailcheck(String email) {
    int cnt = service.duplicatedEmail(email);

    Map<String, String> map = new HashMap<String, String>();
    if (cnt > 0) {
      map.put("str", email + "는 중복되어서 사용할 수 없습니다.");
    } else {
      map.put("str", email + "는 중복아님, 사용가능합니다.");
    }
    return map;
  }

  @GetMapping(value = "/member/idcheck", produces = "application/json;charset=utf-8")
  @ResponseBody
  public Map<String, String> idcheck(String id) {
    int cnt = service.duplicatedId(id);

    Map<String, String> map = new HashMap<String, String>();
    if (cnt > 0) {
      map.put("str", id + "는 중복되어서 사용할 수 없습니다.");
    } else {
      map.put("str", id + "는 중복아님, 사용가능합니다.");
    }
    return map;
  }

  @PostMapping("/member/updateFile")
  public String updateFile(MultipartFile fnameMF, String oldfile, HttpSession session, HttpServletRequest request)
      throws IOException {
    String basePath = UploadMem.getUploadDir();

    if (oldfile != null && !oldfile.equals("member.jpg")) { // 원본파일 삭제
      Utility.deleteFile(basePath, oldfile);
    }

    // storage에 변경 파일 저장
    Map map = new HashMap();
    map.put("id", session.getAttribute("id"));
    map.put("fname", Utility.saveFileSpring(fnameMF, basePath));

    // 디비에 파일명 변경
    int cnt = service.updateFile(map);

    if (cnt == 1) {
      return "redirect:/"; //나중에 뷰페이지 변경할 예정
    } else {
      return "./error";
    }
  }

  @GetMapping("/member/updateFile")
  public String updateFileForm() {

    return "/member/updateFile";
  }
  
  @PostMapping("/member/update")
  public String update(MemberDTO dto, Model model) {
    int cnt = service.update(dto);

    if (cnt == 1) {
      model.addAttribute("id", dto.getId());
      return "redirect:/"; //나중에 뷰페이지 변경할 예정
    } else {
      return "error";
    }
  }

  @GetMapping("/member/update")
  public String update(String id, HttpSession session, Model model) {

    if (id == null) {
      id = (String) session.getAttribute("id");
    }

    MemberDTO dto = service.read(id);

    model.addAttribute("dto", dto);

    return "/member/update";

  }
  
  @GetMapping("/member/findId")
  public String findIdForm() {

    return "/member/findId";

  }
  @ResponseBody
  @GetMapping("/member/findId/check")
  public String findId(@RequestParam(required = false)String mname, 
      @RequestParam(required = false)String email) 
  {
    try {
     
      MemberDTO dto = service.findId(email);
      System.out.println(dto.toString());
      if(dto==null) {
        
        
        return "찾는 아이디가 없습니다";
      }
      else if(dto.getMname().equals(mname)) {
        
        return "찾으시는 아이디"+dto.getId();
      }
      
    }catch(Exception e) {
      System.out.println("실행");
      return "실패";
    }
    return "실패";
   
  }
  
  
  
  @GetMapping("/member/findPasswd")
  public String findPasswd() {

    return "/member/findPasswd";

  }
  
  @ResponseBody
  @GetMapping("/member/findPasswd/check")
  public String findPasswd(@RequestParam(required = false)String id, 
      @RequestParam(required = false)String mname) 
  {
   
   
   
    try {
      MemberDTO dto = service.findPasswd(id);
      if(dto==null) {
        
        return "실패";
      }
      if(dto.getMname().equals(mname)) {
        
        return "찾는비밀번호:"+dto.getPasswd();
      }else {
        
        return "실패";
      }
      
    }catch(Exception e) {
      System.out.println("실행");
      System.out.println(e);
      return "실패tlfvo";
    }

  }
  
  
}
