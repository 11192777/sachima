/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaizai.sachima.util;

/**
 * Java: 无回路有向图(Directed Acyclic Graph)的拓扑排序
 * 该DAG图是通过邻接表实现的。
 * <p>
 * author skywang
 * date 2014/04/22
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ListDG {
    public static class Edge {
        public final Object from;
        public final Object to;

        public Edge(Object from, Object to) {
            this.from = from;
            this.to = to;
        }
    }

    // 邻接表中表对应的链表的顶点
    private static class ENode {
        int ivex;       // 该边所指向的顶点的位置
        ENode nextEdge; // 指向下一条弧的指针
    }

    // 邻接表中表的顶点
    private static class VNode {
        Object data;          // 顶点信息
        ENode firstEdge;    // 指向第一条依附该顶点的弧
    }

    private final List<VNode> mVexs;  // 顶点数组

    /*
     * 创建图(用已提供的矩阵)
     *
     * 参数说明：
     *     vexs  -- 顶点数组
     *     edges -- 边数组
     */
    public ListDG(List<?> vexs, List<Edge> edges) {

        // 初始化"顶点"
        mVexs = new ArrayList<>();
        for (Object vex : vexs) {
            // 新建VNode
            VNode vnode = new VNode();
            vnode.data = vex;
            vnode.firstEdge = null;
            // 将vnode添加到数组mVexs中
            mVexs.add(vnode);
        }

        // 初始化"边"
        for (Edge edge : edges) {
            // 读取边的起始顶点和结束顶点
            // 读取边的起始顶点和结束顶点
            int p1 = getPosition(edge.from);
            int p2 = getPosition(edge.to);

            // 初始化node1
            ENode node1 = new ENode();
            node1.ivex = p2;
            // 将node1链接到"p1所在链表的末尾"
            if (mVexs.get(p1).firstEdge == null)
                mVexs.get(p1).firstEdge = node1;
            else
                linkLast(mVexs.get(p1).firstEdge, node1);
        }
    }

    /*
     * 将node节点链接到list的最后
     */
    private void linkLast(ENode list, ENode node) {
        ENode p = list;

        while (p.nextEdge != null)
            p = p.nextEdge;
        p.nextEdge = node;
    }

    /*
     * 返回ch位置
     */
    private int getPosition(Object ch) {
        for (int i = 0; i < mVexs.size(); i++)
            if (mVexs.get(i).data == ch)
                return i;
        return -1;
    }


    /*
     * 打印矩阵队列图
     */
    public void print() {
        for (VNode mVex : mVexs) {
            ENode node = mVex.firstEdge;
            while (node != null) {
                node = node.nextEdge;
            }
        }
    }

    /*
     * 拓扑排序
     *
     * 返回值：
     *     true 成功排序，并输入结果
     *     false 失败(该有向图是有环的)
     */
    public boolean topologicalSort(Object[] tops) {
        int index = 0;
        int num = mVexs.size();
        int[] ins;               // 入度数组
        //Object[] tops;             // 拓扑排序结果数组，记录每个节点的排序后的序号。
        Queue<Integer> queue;    // 辅组队列

        ins = new int[num];
        queue = new LinkedList<>();

        // 统计每个顶点的入度数
        for (VNode mVex : mVexs) {

            ENode node = mVex.firstEdge;
            while (node != null) {
                ins[node.ivex]++;
                node = node.nextEdge;
            }
        }

        // 将所有入度为0的顶点入队列
        for (int i = 0; i < num; i++)
            if (ins[i] == 0)
                queue.offer(i);                 // 入队列

        while (!queue.isEmpty()) {              // 队列非空
            int j = queue.poll();    // 出队列。j是顶点的序号
            tops[index++] = mVexs.get(j).data;  // 将该顶点添加到tops中，tops是排序结果
            ENode node = mVexs.get(j).firstEdge;// 获取以该顶点为起点的出边队列

            // 将与"node"关联的节点的入度减1；
            // 若减1之后，该节点的入度为0；则将该节点添加到队列中。
            while (node != null) {
                // 将节点(序号为node.ivex)的入度减1。
                ins[node.ivex]--;
                // 若节点的入度为0，则将其"入队列"
                if (ins[node.ivex] == 0)
                    queue.offer(node.ivex);    // 入队列

                node = node.nextEdge;
            }
        }

        return index == num;
    }

}