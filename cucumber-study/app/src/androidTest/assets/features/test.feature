Feature: 主界面测试

    Scenario: 测试一
        Given 进入主界面
        When 点击测试一
        Then 显示测试一已点击
        Then 退出主界面

    Scenario: 测试二
        Given 进入主界面
        When 点击测试二
        Then 显示测试二已点击
        Then 退出主界面

    Scenario: 测试三
        Given 进入主界面
        When 在.搜索框中输入123456789
        And 点击测试三
        Then 显示123456789
        Then 退出主界面

    Scenario: 测试四
        Given 进入主界面
        When 滚动到显示测试四
        And 点击测试四
        Then 显示测试四已点击
        Then 退出主界面

    Scenario: 测试五
        Given 进入主界面
        When 点击测试五
        Then 控件.tv_status显示文字测试五已点击
        Then 退出主界面