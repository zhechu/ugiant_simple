package com.ugiant.jfinalbase;


import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.render.ViewType;
import com.ugiant.constant.base.SessionAttriKey;
import com.ugiant.constant.base.Table;
import com.ugiant.jfinalext.interceptor.AuthInterceptor;
import com.ugiant.jfinalext.model.base.LoginUserInfo;
import com.ugiant.jfinalext.model.tpb.TpbDepartment;
import com.ugiant.jfinalext.model.tpb.TpbDepartmentUser;
import com.ugiant.jfinalext.model.tpb.TpbMenu;
import com.ugiant.jfinalext.model.tpb.TpbMenuBtn;
import com.ugiant.jfinalext.model.tpb.TpbRole;
import com.ugiant.jfinalext.model.tpb.TpbRoleMenu;
import com.ugiant.jfinalext.model.tpb.TpbRoleMenuBtn;
import com.ugiant.jfinalext.model.tpb.TpbRoleUser;
import com.ugiant.jfinalext.model.tpb.TpbSysConstant;
import com.ugiant.jfinalext.model.tpb.TpbSysUser;
import com.ugiant.jfinalext.route.AdminRoute;
import com.ugiant.jfinalext.service.tpb.SystemService;

/**
 * jfinal 基础类
 * @author lingyuwang
 *
 */
public class BaseConfig extends JFinalConfig {

	@Override
	public void configConstant(Constants me) {
		PropKit.use("config.properties");
		me.setDevMode(PropKit.getBoolean("devMode", false));
		me.setViewType(ViewType.FREE_MARKER);
	}

	@Override
	public void configRoute(Routes me) {
		me.add(new AdminRoute()); // 后台路由
	}

	@Override
	public void configPlugin(Plugins me) {
		// 配置C3p0数据库连接池插件
		C3p0Plugin c3p0Plugin = new C3p0Plugin(PropKit.get("jdbcUrl", ""),PropKit.get("user", ""), PropKit.get("password", "").trim());
		me.add(c3p0Plugin);
		// 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);
		arp.setShowSql(true);
		me.add(arp);
		
		arp.addMapping(Table.SYS_CONSTANT, TpbSysConstant.class);
		arp.addMapping(Table.TPB_SYS_USER, TpbSysUser.class);
		arp.addMapping(Table.TPB_DEPARTMENT, TpbDepartment.class);
		arp.addMapping(Table.TPB_DEPARTMENT_USER, TpbDepartmentUser.class);
		arp.addMapping(Table.TPB_ROLE, TpbRole.class);
		arp.addMapping(Table.TPB_ROLE_USER, TpbRoleUser.class);
		arp.addMapping(Table.TPB_MENU, TpbMenu.class);
		arp.addMapping(Table.TPB_MENU_BTN, TpbMenuBtn.class);
		arp.addMapping(Table.TPB_ROLE_MENU, TpbRoleMenu.class);
		arp.addMapping(Table.TPB_ROLE_MENU_BTN, TpbRoleMenuBtn.class);
	}

	@Override
	public void configInterceptor(Interceptors me) {
		me.add(new AuthInterceptor()); // 权限拦截
		
		// 获取当前菜单或上级菜单，用于调整当前菜单样式
		me.add(new Interceptor() {
			public void intercept(Invocation inv) {
				Controller controller = inv.getController();
				LoginUserInfo loginUserInfo = (LoginUserInfo) controller.getSession().getAttribute(SessionAttriKey.LOGIN_USER_INFO);
				if (loginUserInfo != null) {
					inv.getController().setAttr("username", loginUserInfo.getUsername());
				}
				String rpath = controller.getRequest().getServletPath();
				// 获取当前菜单或上级菜单 ids
				String menuIds = SystemService.service.findMenuParentIdsByLinkUrl(rpath);
				if (menuIds != null) {
					controller.setAttr("menuIds", menuIds);
				}
				inv.invoke();
			}
		});
	}
	
	@Override
	public void configHandler(Handlers me) {
		
	}
	
	public static void main(String[] args){
		JFinal.start("WebRoot", 80, "/", 5);
	}

}
