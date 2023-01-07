package org.akvo.search.common.spi;

import org.akvo.search.common.entity.Config;
import org.akvo.search.common.entity.ExecResult;


public interface FrontendFacade {
    /**
     * 在后端接口实例化之后会调用此接口将自身返回，以便于前端调用
     *
     * @param backend 后端实现
     */
    void backend(BackendFacade backend);

    /**
     * 后端在搜索完毕后会调用该方法回执结果
     *
     * @param result 搜索结果
     */
    void publish(ExecResult result);

    /**
     * 获取config
     *
     * @return config
     */
    Config config();

    /**
     * 提供当前实现的名字
     *
     * @return 当前实现的名字
     */
    String name();

    /**
     * 启动点
     */
    void start();

}
