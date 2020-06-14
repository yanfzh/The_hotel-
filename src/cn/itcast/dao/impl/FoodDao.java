package cn.itcast.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.dao.IFoodDao;
import cn.itcast.entity.Food;
import cn.itcast.utils.Condition;
import cn.itcast.utils.JdbcUtils;
import cn.itcast.utils.PageBean;

public class FoodDao implements IFoodDao{

	@Override
	public Food findById(int id) {
		String sql = "select * from food where id=?";
//		StringBuffer sb = new StringBuffer();
//		sb.append("select");
//		sb.append("		f.id,");
//		sb.append("		f.foodName,");
//		sb.append("		f.price,");
//		sb.append("		f.mprice,");
//		sb.append("		f.remark,");
//		sb.append("		f.img,");
//		sb.append("		f.foodType_id,");
//		sb.append("		t.typeName ");
//		sb.append("from ");
//		sb.append("		food f, ");
//		sb.append("		foodtype t ");
//		sb.append("where 1=1");
//		sb.append("		and f.foodType_id=t.id ");
		try {
			return JdbcUtils.getQuerrRunner().query(sql, new BeanHandler<Food>(Food.class), id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public void getAll(PageBean<Food> pb) {
		int t = 0;
		// 获取条件对象
		Condition condition = pb.getCondition();
		// 条件之类别id
		int typeId = condition.getFoodTypeId();
		// 条件之菜品名称
		String foodName = condition.getFoodName();
		System.out.println("test "+foodName);
		StringBuffer sb = new StringBuffer();
		sb.append("select ");
		sb.append("f.id,");
		sb.append("f.foodName,");
		sb.append("f.price,");
		sb.append("f.mprice,");
		sb.append("f.remark,");
		sb.append("f.img,");
		sb.append("f.foodType_id,");
		sb.append("t.typeName ");
		sb.append("from ");
		sb.append("food f,");
		sb.append("foodtype t ");
		sb.append("where 1=1 ");
		sb.append("and f.foodType_id=t.id ");
		// 存储查询条件对应的值
		List<Object> list = new ArrayList<Object>();
		/*******拼接查询条件*********/
		// 类别id条件
		if (typeId > 0) {
			sb.append("and f.foodType_id=? ");
			list.add(typeId);  // 组装条件值
		}
		// 菜品名称
		if (foodName != null && !"".equals(foodName.trim())) {
			sb.append("and f.foodName like '%"+foodName+"%'");
			//list.add("%"+foodName+"%");    // 组装条件值
			t=1;
		}
		
		/*********分页条件**********/
		sb.append(" LIMIT ?,?");
		
		
		/*****判断：当当前页< 1， 设置当前页为1；  当当前页>总页数，设置当前页为总页数******/
		// 先查询总记录数
		int totalCount = getTotalCount(pb);  //?
		System.out.println(totalCount);
		// 设置分页bean参数之总记录数
		pb.setTotalCount(totalCount);
		
		if(pb.getCurrentPage() < 1) {
			pb.setCurrentPage(1);
		} else if (pb.getCurrentPage() > pb.getTotalPage()) {
			pb.setCurrentPage(pb.getTotalPage());
		}
		
		// 起始行
		int index = (pb.getCurrentPage() - 1) * pb.getPageCount();
		// 返回记录行
		int count = pb.getPageCount();
		if(t==1) {
			list.add(0);		    // 组装条件值 - 起始行
		    list.add(count);
			t=0;
		}else {
			list.add(index);		    // 组装条件值 - 起始行
		    list.add(count);
		}
				    // 组装条件值 - 查询返回的行
		
		
		// 按条件、分页查询
		try {
			List<Food> pageData = JdbcUtils.getQuerrRunner().
				query(sb.toString(), new BeanListHandler<Food>(Food.class),list.toArray());
			// 把查询到的数据设置到分页对象中
			pb.setPageData(pageData);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public int getTotalCount(PageBean<Food> pb) {
		// 获取条件对象
		Condition condition = pb.getCondition();
		// 条件之类别id
		int typeId = condition.getFoodTypeId();
		// 条件之菜品名称
		String foodName = condition.getFoodName();
		
		StringBuffer sb = new StringBuffer();
		sb.append("select");
		sb.append("		count(*) ");
		sb.append("from ");
		sb.append("		food f, ");
		sb.append("		foodtype t ");
		sb.append("where 1=1");
		sb.append("		and f.foodType_id=t.id ");
		// 存储查询条件对应的值
		List<Object> list = new ArrayList<Object>();
		/*******拼接查询条件*********/
		// 类别id条件
		if (typeId > 0) {
			sb.append("	and f.foodType_id=?");
			list.add(typeId);  // 组装条件值
		}
		// 菜品名称
		if (foodName != null && !"".equals(foodName.trim())) {
			sb.append("  and f.foodName like ?");
			list.add(foodName);    // 组装条件值
		}
		
		try {
			// 查询
			Long num = JdbcUtils.getQuerrRunner().query(sb.toString(), new ScalarHandler<Long>(),list.toArray());
			return num.intValue();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public List<Food> findAllFood() {
		String sql = "select * from food";
	
		try {
			return JdbcUtils.getQuerrRunner().query(sql, new BeanListHandler<Food>(Food.class));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public void delete(int id) {
		String sql = "delete from food where id=?";
		try {
			JdbcUtils.getQuerrRunner().update(sql, id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public void save(Food food) {
		String sql = "INSERT INTO food(foodName,foodType_id,price,mprice,remark,img) VALUES(?,?,?,?,?,?)";
		Object[] params={food.getFoodName(),food.getFoodType_id(),food.getPrice(),food.getMprice(),food.getRemark(),food.getImg()};
		
		try {
			JdbcUtils.getQuerrRunner().update(sql,params);
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}

	@Override
	public void update(Food food) {
		String sql = "update food set foodName=?,foodType_id=?,price=?,mprice=?,remark=?,img=? where id=?";
		System.out.println("set"+food.getImg());
		Object[] params={food.getFoodName(),food.getFoodType_id(),food.getPrice(),food.getMprice(),food.getRemark(),food.getImg(),food.getId()};
		try {
			JdbcUtils.getQuerrRunner().update(sql,params);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public List<Food> search(String keyword) {
		String sql = "select * from food where foodName like ?";
	
		try {
			return JdbcUtils.getQuerrRunner().query(sql, new BeanListHandler<Food>(Food.class),"%"+keyword+"%");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}




















