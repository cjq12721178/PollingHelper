package com.example.kat.pollinghelper.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.kat.pollinghelper.utility.TreeNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by KAT on 2016/7/8.
 * 使普通ListView产生树形结构，每一层支持多种布局
 * 注意，若给在某个子层布局中的某个控件添加OnClickListener事件，则将覆盖收缩扩展事件
 * 此时，若设置了indicator，则点击相应indicator还是可以触发该事件
 */
public class TreeViewAdapter<E extends TreeNode> extends BaseAdapter {

    private class OnItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            shrink(position);
        }

        public OnItemClickListener setPosition(int position) {
            this.position = position;
            return this;
        }

        private int position;
    }

    //用于header view的自定义样式
    public interface OnNodeViewCreateListener {
        void onCreate(View convertView);
    }

    private class AutoGenerateNode implements TreeNode {

        public AutoGenerateNode(int layout, int viewType,
                                OnNodeViewCreateListener l) {
            this.layout = layout;
            this.viewType = viewType;
            onViewCreateListener = l;
        }

        @Override
        public View getView(Context context, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(layout, parent, false);
                if (onViewCreateListener != null) {
                    onViewCreateListener.onCreate(convertView);
                }
            }
            return convertView;
        }

        @Override
        public boolean isExpanded() {
            return false;
        }

        @Override
        public void setExpanded(boolean expanded) {

        }

        @Override
        public List getChildren() {
            return null;
        }

        @Override
        public int getViewType() {
            return viewType;
        }

        private OnNodeViewCreateListener onViewCreateListener;
        private int viewType;
        private int layout;
    }

    private class ViewHolder {
        private ImageView indicator;
        private View content;
        private OnItemClickListener listener;
    }

    public TreeViewAdapter(Context context) {
        this.context = context;
        viewTypes = new HashMap<>();
    }

    public TreeViewAdapter(Context context, List<TreeNode> nodes) {
        this(context);
        setDataSource(nodes);
    }

    public void setDataSource(List<TreeNode> nodes) {
        this.nodes = nodes;
        gatherViewType(nodes, 0);
        maxLevel = getMaxLevel();
        setTopHeader();
    }

    @Override
    public int getCount() {
        return nodes != null ? nodes.size() : 0;
    }

    @Override
    public TreeNode getItem(int position) {
        return nodes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return viewTypes.size();
    }

    private void gatherViewType(List<TreeNode> children, int level) {
        if (viewTypes == null || children == null)
            return;

        for (TreeNode child :
                children) {
            viewTypes.put(child.getViewType(), level);
            //顺带清除收缩信息
            child.setExpanded(false);
            gatherViewType(child.getChildren(), level + 1);
        }
    }

    private int getMaxLevel() {
        int levelMax = 0;
        for (Integer level :
                viewTypes.values()) {
            if (levelMax < level) {
                levelMax = level;
            }
        }
        return levelMax;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TreeNode node = getItem(position);
        if (isIndicatorUsed() && !isNodeAtMaxLevel(node)) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                //生成载体
                LinearLayout baseLayout = new LinearLayout(context);
                baseLayout.setOrientation(LinearLayout.HORIZONTAL);
                viewHolder = new ViewHolder();
                viewHolder.indicator = new ImageView(context);
                baseLayout.addView(viewHolder.indicator);
                //生成实际内容
                viewHolder.content = node.getView(context, viewHolder.content, baseLayout);
                baseLayout.addView(viewHolder.content);
                //设置指示图标参数
                viewHolder.content.measure(0, 0);
                int indicatorSideLength = viewHolder.content.getMeasuredHeight();
                viewHolder.indicator.setLayoutParams(new LinearLayout.LayoutParams(indicatorSideLength, indicatorSideLength));
                int indicatorPadding = indicatorSideLength / 6;
                viewHolder.indicator.setPadding(indicatorPadding, indicatorPadding, indicatorPadding, indicatorPadding);
                viewHolder.indicator.setImageDrawable(expandedIndicator);
                viewHolder.indicator.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                //生成点击监听器
                viewHolder.listener = new OnItemClickListener();
                viewHolder.indicator.setOnClickListener(viewHolder.listener);
                baseLayout.setOnClickListener(viewHolder.listener);
                baseLayout.setPadding(getIndent(indicatorSideLength, node), 0, 0, 0);
                baseLayout.setTag(viewHolder);
                convertView = baseLayout;
            } else {
                viewHolder = (ViewHolder)convertView.getTag();
                viewHolder.content = node.getView(context, viewHolder.content, (LinearLayout) convertView);
            }
            viewHolder.listener.setPosition(position);
            viewHolder.indicator.setImageDrawable(hasChild(node) ?
                    (node.isExpanded() ? expandedIndicator : collapsedIndicator) :
                    null);
        } else {
            View realView = node.getView(context, convertView, null);
            if (convertView == null) {
                realView.setPadding(getIndent(realView, node), 0, 0, 0);
            }
            convertView = realView;
            if (hasChild(node)) {
                convertView.setOnClickListener(new OnItemClickListener().setPosition(position));
            }
        }

        return convertView;
    }

    private boolean isIndicatorUsed() {
        return expandedIndicator != null && collapsedIndicator != null;
    }

    private boolean isNodeAtMaxLevel(TreeNode node) {
        return viewTypes.get(node.getViewType()) == maxLevel;
    }

    private int getViewLevel(TreeNode node) {
        return viewTypes.get(node.getViewType());
    }

    private int getIndent(View realView, TreeNode node) {
        int level =  getViewLevel(node) + (isIndicatorUsed() ? 1 : 0);

        if (indent > 0)
            return indent * level;

        if (realView == null)
            return 0;

        realView.measure(0, 0);
        return realView.getMeasuredHeight() * level;
    }

    //用于有indicator的情况，有时间把这个和上面那童鞋合并一下
    private int getIndent(int indicatorWidth, TreeNode currentNode) {
        return (indent > 0 ? indent : indicatorWidth) * getViewLevel(currentNode);
    }

    //用于外界测量header缩进值
    public int getIndent(View measuredView, int level) {
        if (measuredView == null)
            return 0;

        measuredView.measure(0, 0);
        int measuredHeight = measuredView.getMeasuredHeight();
        int extraLength = isIndicatorUsed() ? measuredHeight : 0;

        return (indent > 0 ? indent : measuredHeight) * level + extraLength;
    }

    private boolean hasChild(TreeNode node) {
        return node.getChildren() != null && node.getChildren().size() > 0;
    }

    public void shrink(int position) {
        if (position < 0 || position >= nodes.size())
            return;

        TreeNode child = getItem(position);
        if (!hasChild(child))
            return;

        if (child.isExpanded()) {
            collapse(child, ++position);
        } else {
            expand(child, ++position);
        }
        notifyDataSetChanged();
    }

    private int expand(TreeNode child, int rootLocation) {
        if (!hasChild(child))
            return rootLocation;

        int position = rootLocation;
        List<TreeNode> grandchildren = child.getChildren();
        TreeNode header = getChildViewHeader(child);
        if (header != null) {
            nodes.add(position++, header);
        }
        for (TreeNode grandchild:
                grandchildren) {
            nodes.add(position++, grandchild);
            if (grandchild.isExpanded()) {
                position = expand(grandchild, position);
            }
        }
        child.setExpanded(true);
        return position;
    }

    private TreeNode getChildViewHeader(TreeNode parent) {
        return headers != null ? headers.get(getViewLevel(parent) + 1) : null;
    }

    private void collapse(TreeNode child, int rootLocation) {
        if (!hasChild(child))
            return;

        List<TreeNode> grandchildren = child.getChildren();
        TreeNode header = getChildViewHeader(child);
        if (header != null) {
            nodes.remove(rootLocation);
        }
        for (TreeNode grandchild :
                grandchildren) {
            nodes.remove(rootLocation);
            if (grandchild.isExpanded()) {
                collapse(grandchild, rootLocation);
            }
        }
        child.setExpanded(false);
    }

    public void setIndicator(int expand, int collapse) {
        setIndicator(context.getResources().getDrawable(expand),
                context.getResources().getDrawable(collapse));
    }

    public void setIndicator(Drawable expand, Drawable collapse) {
        expandedIndicator = expand;
        collapsedIndicator = collapse;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public void addHeader(int level, TreeNode header) {
        if (level < 0 || header == null)
            return;

        if (headers == null) {
            headers = new HashMap<>();
        }

        if (level == 0) {
            setTopHeader(header);
        }

        headers.put(level, header);
        viewTypes.put(header.getViewType(), level);
    }

    public void addHeader(int level, int headerLayout, int viewType,
                          OnNodeViewCreateListener l) {
        addHeader(level, new AutoGenerateNode(headerLayout, viewType, l));
    }

    //用于addHeader
    private void setTopHeader(TreeNode header) {
        if (nodes == null)
            return;

        if (header == null)
            return;

        if (headers.get(0) != null) {
            nodes.set(0, header);
        } else {
            nodes.add(0, header);
        }
    }

    //用于setDataSource
    private void setTopHeader() {
        if (headers == null)
            return;

        TreeNode topHeader = headers.get(0);
        if (topHeader != null) {
            nodes.add(0, topHeader);
        }
    }

    private int maxLevel;
    private Map<Integer, Integer> viewTypes;
    private Map<Integer, TreeNode> headers;
    private int indent;
    private Drawable expandedIndicator;
    private Drawable collapsedIndicator;
    private Context context;
    private List<TreeNode> nodes;
}
