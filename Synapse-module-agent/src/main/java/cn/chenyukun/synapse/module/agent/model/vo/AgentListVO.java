package cn.chenyukun.synapse.module.agent.model.vo;

import java.io.Serializable;
import java.util.List;

/**
 * Agent列表响应VO
 */
public class AgentListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
    private List<AgentListItemVO> agents;

    public AgentListVO() {
    }

    public AgentListVO(Long total, Integer page, Integer pageSize, Integer totalPages, List<AgentListItemVO> agents) {
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.agents = agents;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<AgentListItemVO> getAgents() {
        return agents;
    }

    public void setAgents(List<AgentListItemVO> agents) {
        this.agents = agents;
    }
}

