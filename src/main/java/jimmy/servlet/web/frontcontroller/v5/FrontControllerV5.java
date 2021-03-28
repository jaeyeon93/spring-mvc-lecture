package jimmy.servlet.web.frontcontroller.v5;

import jimmy.servlet.web.frontcontroller.ModelView;
import jimmy.servlet.web.frontcontroller.MyView;
import jimmy.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import jimmy.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import jimmy.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;
import jimmy.servlet.web.frontcontroller.v4.controller.MemberFormControllerV4;
import jimmy.servlet.web.frontcontroller.v4.controller.MemberListControllerV4;
import jimmy.servlet.web.frontcontroller.v4.controller.MemberSaveControllerV4;
import jimmy.servlet.web.frontcontroller.v5.adapter.ControllerV3HandlerAdaptor;
import jimmy.servlet.web.frontcontroller.v5.adapter.ControllerV4HandlerAdaptor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "frontControllerServletV5", urlPatterns = "/front-controller/v5/*")
public class FrontControllerV5 extends HttpServlet {

    private final Map<String, Object> handlerMappingMap = new HashMap<>(); // controlelrV3, V4를 넣기 위해 Object로
    private final List<MyHandlerAdapter> handlerAdapters = new ArrayList<>();

    public FrontControllerV5() {
        initHandlerMappingMap();
        initHandlerAdapters();
    }

    private void initHandlerAdapters() {
        handlerAdapters.add(new ControllerV3HandlerAdaptor());
        handlerAdapters.add(new ControllerV4HandlerAdaptor());
    }

    private void initHandlerMappingMap() {
        handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members/save", new MemberSaveControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerV3());

        // v4관련 핸들러
        handlerMappingMap.put("/front-controller/v5/v4/members/new-form", new MemberFormControllerV4());
        handlerMappingMap.put("/front-controller/v5/v4/members/save", new MemberSaveControllerV4());
        handlerMappingMap.put("/front-controller/v5/v4/members", new MemberListControllerV4());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object handler = getHandler(request); // 핸들러 조회

        if (handler == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        MyHandlerAdapter adaptor = getHandlerAdaptor(handler);
        ModelView mv = adaptor.handle(request, response, handler);
        String viewName = mv.getViewName();
        MyView view = viewResolver(viewName);
        view.render(mv.getModel(), request, response);
    }

    private MyHandlerAdapter getHandlerAdaptor(Object handler) {
        for (MyHandlerAdapter adapter : handlerAdapters) {
            if (adapter.supports(handler))
                return adapter;
        }
        throw new IllegalArgumentException("handler adaptor를 찾을 수 없습니다.");
    }

    private Object getHandler(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return handlerMappingMap.get(requestURI);
    }

    private MyView viewResolver(String viewName) {
        MyView view = new MyView("/WEB-INF/views/" + viewName + ".jsp");
        return view;
    }
}
