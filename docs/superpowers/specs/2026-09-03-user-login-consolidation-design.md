# 用户登录合并设计

## 目标

在不改变管理端 `/login`、病例端 `/biz/user-auth/login`、JWT 格式和登录后 Spring Security 鉴权链路的前提下，将两端重复的登录业务逻辑统一为一个服务。

## 方案

新增 `UserLoginService` 作为唯一的 HTTP 登录业务入口。它接收用户名、密码、验证码、验证码 UUID 和用户类型，完成验证码校验、通用登录前置校验、用户加载、密码匹配、登录信息更新和 Token 签发。

管理员账户仍使用 `SysUser`、`ISysUserService`、`UserDetailsServiceImpl` 和管理员权限集合；医生、患者仍使用 `UserEntity`、`UserMapper` 和病例端权限集合。不同数据对象仅在用户加载处转换为统一的 `LoginUser`，不会改变表结构或 API 请求、响应格式。

## 行为边界

- 管理端保留失败次数限制、失败计数清理、登录成功/失败日志和 IP 黑名单校验。
- 医生、患者保留当前允许的注册、待审核、审核失败等登录状态；仅停用状态禁止登录。
- 所有登录密码匹配通过 Spring Security 管理的 `PasswordEncoder` Bean 完成。
- `AuthenticationManager` 不再用于管理端 HTTP 登录；`UserDetailsServiceImpl` 继续用于管理员账户的加载与状态判定。
- JWT 过滤器、`SecurityFilterChain`、`TokenService`、`@CurrLoginUser` 和前端接口路径不做修改。

## 验证

- 覆盖医生/患者登录成功、用户不存在、密码错误、停用用户。
- 覆盖管理员登录成功、密码错误时记录失败次数、成功时清理失败计数。
- 编译并运行登录相关单元测试。
