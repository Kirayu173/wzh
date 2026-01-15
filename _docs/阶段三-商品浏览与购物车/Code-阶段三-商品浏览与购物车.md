# 阶段三代码文档（商品浏览与购物车）

## 1. 代码结构概览

- Android 客户端：`android/`
  - 商品列表：`android/app/src/main/java/com/wzh/suyuan/feature/home/`
  - 商品详情：`android/app/src/main/java/com/wzh/suyuan/feature/product/`
  - 购物车：`android/app/src/main/java/com/wzh/suyuan/feature/cart/`
  - 本地缓存（Room）：`android/app/src/main/java/com/wzh/suyuan/data/db/`
  - 网络模型与接口：`android/app/src/main/java/com/wzh/suyuan/network/`
- 后端服务：`backend/`
  - 商品与购物车接口：`backend/src/main/java/com/wzh/suyuan/backend/controller/`
  - 业务服务层：`backend/src/main/java/com/wzh/suyuan/backend/service/`
  - 数据实体与仓库：`backend/src/main/java/com/wzh/suyuan/backend/entity/`、`backend/src/main/java/com/wzh/suyuan/backend/repository/`

## 2. Android 端实现说明

### 2.1 商品列表

- 页面：`android/app/src/main/java/com/wzh/suyuan/feature/home/HomeFragment.java`
  - `SwipeRefreshLayout + RecyclerView` 实现下拉刷新与分页加载
  - 点击列表项进入商品详情
  - 网络失败时优先加载本地缓存并提示
- 适配器：`android/app/src/main/java/com/wzh/suyuan/feature/home/ProductAdapter.java`
  - 使用 Glide 加载商品封面
  - 展示商品名称、产地、库存与价格

### 2.2 商品详情

- 页面：`android/app/src/main/java/com/wzh/suyuan/feature/product/ProductDetailActivity.java`
  - `ViewPager2` 图片轮播 + 指示器
  - 展示名称、价格、库存、产地与描述
  - 数量选择 + “加入购物车”操作
- 逻辑：`android/app/src/main/java/com/wzh/suyuan/feature/product/ProductDetailPresenter.java`
  - 详情请求失败时读取本地缓存
  - 加入购物车失败时写入本地缓存（离线可用）

### 2.3 购物车

- 页面：`android/app/src/main/java/com/wzh/suyuan/feature/cart/CartFragment.java`
  - 商品数量调整、选中状态更新与删除
  - 失败回滚：数量/选中操作失败时恢复原状态
  - 展示合计金额
- 逻辑：`android/app/src/main/java/com/wzh/suyuan/feature/cart/CartPresenter.java`
  - 服务端成功后更新本地缓存
  - 网络异常时展示本地缓存并提示

### 2.4 本地缓存与持久化

- Room 数据库：`android/app/src/main/java/com/wzh/suyuan/data/db/AppDatabase.java`
  - Product / Cart 数据持久化
  - BigDecimal 通过 `DbConverters` 转换
- 映射工具：`android/app/src/main/java/com/wzh/suyuan/data/product/ProductMapper.java`、`android/app/src/main/java/com/wzh/suyuan/data/cart/CartMapper.java`

## 3. 后端实现说明

### 3.1 商品接口

- 控制器：`backend/src/main/java/com/wzh/suyuan/backend/controller/ProductController.java`
  - `GET /products`：分页列表
  - `GET /products/{id}`：详情
- 服务：`backend/src/main/java/com/wzh/suyuan/backend/service/ProductService.java`
  - 图片列表优先读取 `product_image` 表；无数据时回落 `cover_url`

### 3.2 购物车接口

- 控制器：`backend/src/main/java/com/wzh/suyuan/backend/controller/CartController.java`
  - `GET /cart`、`POST /cart`、`PUT /cart/{id}`、`PATCH /cart/{id}/select`、`DELETE /cart/{id}`
- 服务：`backend/src/main/java/com/wzh/suyuan/backend/service/CartService.java`
  - 购物车与用户绑定
  - 库存不足返回 409

### 3.3 数据模型

- 商品：`backend/src/main/java/com/wzh/suyuan/backend/entity/Product.java`
- 商品图片：`backend/src/main/java/com/wzh/suyuan/backend/entity/ProductImage.java`
- 购物车：`backend/src/main/java/com/wzh/suyuan/backend/entity/CartItem.java`

## 4. 关键决策与配置

- 轮播组件：使用 `ViewPager2` 实现，无额外第三方依赖。
- 价格字段：后端使用 `BigDecimal`，Android 侧保持同类型并通过 Room 转换。
- 购物车持久化：后端存储为主，Android 本地缓存用于离线兜底。

## 5. 测试说明

- 后端接口测试：`backend/src/test/java/com/wzh/suyuan/backend/ProductCartControllerTest.java`
  - 覆盖商品列表、详情、购物车增删改查

## 6. 运行建议

- Android：使用 Android Studio 打开 `android/`，登录后进入首页即可浏览商品。
- 后端：在 `backend/` 执行 `mvn spring-boot:run`，确保数据库可用并配置好 `application.yml`。
