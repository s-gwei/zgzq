
let dataModel = window.dataModel = new ht.DataModel();
let g2d = window.graph = new ht.graph.GraphView(dataModel);//定义2d图纸
let g3d = window.g3d = new ht.graph3d.Graph3dView();//定义3d模型

g3d.setMovableFunc(() => false)
g2d.setMovableFunc(() => false);

g3d.addToDOM();
g2d.addToDOM(g3d.getView());

// 选中边框为0
g2d.getSelectWidth = function () {
  return 0;
};
// 禁止鼠标缩放
g2d.handleScroll = function () {
};
// 禁止 touch 下双指缩放
g2d.handlePinch = function () {
};
// 禁止平移
g2d.setPannable(false);
// 禁止框选
// g2d.setRectSelectable(false);
// 隐藏滚动条
g2d.setScrollBarVisible(false);
// 禁止图元移动

function getWheelEventName(element){
  if ('onwheel' in element) {
    // 标准事件
    return 'wheel';
  } else if (document.onmousewheel !== undefined) {
    // 通用旧版事件
    return 'mousewheel';
  } else {
    // 旧版 Firefox 事件
    return 'DOMMouseScroll';
  }
}
function interaction2D3D (gv) {
  let div2d = gv.getView()
  let handler = event => {
    if (gv.getDataAt(event)) {
      event.preventDefault()
      event.stopPropagation()
    }
  }
  div2d.addEventListener('mousedown', handler)
  div2d.addEventListener('touchstart', handler)
  div2d.addEventListener(getWheelEventName(div2d) , handler)
}
// 避免23d交互重叠
interaction2D3D(g2d);
g3d.dm().clear()
g3d.deserialize('scenes/daba.json', (json, dm, gv, datas) => {
  g3d.setEye([300, 300, 900])
  let boat = this.boat = dm.getDataByTag('boat')
})

g2d.dm().clear()
g2d.deserialize('displays/hjx/hjx_mainGraph.json', (json, dm, g2d, datas) => {
  let videoWin = this.videoWin = dm.getDataByTag('video_win')
  //
  let closeBtn = this.closeBtn = dm.getDataByTag('close_video')
})

g2d.mi(e => {
  const kind = e.kind // 事件类型
  if (kind === 'clickData') {
    if (e.data === this.closeBtn) {
      this.videoWin.s('2d.visible', false)
    }
  }
});

//双击查看
g3d.mi(e => {
  const kind = e.kind
  if (kind === 'clickData') {
    if (e.data === this.boat) {
      this.videoWin.s('2d.visible', true)
    }
  }
});
