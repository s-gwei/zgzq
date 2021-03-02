//创建调度，地球旋转
let earthAnimation = {
  interval: 100,
  action: function (data) {
    let earth = g3d.dm().getDataByTag('earth')
    if (data !== earth) {
      return
    }
    let old = earth.r3() // rotation3d
    earth.r3([old[0], old[1] + Math.PI / 180, old[2]])
  }
}

let dataModel = window.dataModel = new ht.DataModel();
let g2d = window.graph = new ht.graph.GraphView(dataModel);//定义2d图纸
let g3d = window.g3d = new ht.graph3d.Graph3dView();//定义3d模型

g2d.setMovableFunc(() => false);

g3d.addToDOM();
// g2d.addToDOM(g3d.getView());

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
// g2d.dm().clear()
// g2d.deserialize('displays/hjx/lianjiedong/ditu_main.json',(json,dm,g3d,datas) =>{
//   // 反序列化图纸完成回调
// });//反序列化图纸

g3d.dm().clear()
g3d.deserialize('scenes/earth.json', (json, dm, gv, datas) => {
  let earthNode = this.earthNode = dm.getDataByTag('earth')
  let allLabel = this.allLabel = dm.getDataByTag('all_label')
  let hjxLabel = this.hjxLabel = dm.getDataByTag('hjx_label')
  let shkLabel = this.shkLabel = dm.getDataByTag('shk_label')
  g3d.setEye([100, 300, 1400])
  // g3d.getView().style.top = 80
  g3d.dm().addScheduleTask(earthAnimation)// 添加调度
  // graphDiv.style.marginLeft = '256px'
})

g2d.mi(e => {
  const kind = e.kind

});
//双击查看
g3d.mi(e => {
  const kind = e.kind
  if (kind === 'clickData') {
    if (e.data === this.allLabel) {
      // window.parent.postMessage('/shk/main', '*');
    } else if (e.data === this.hjxLabel) {
      // 跳转到黄金峡分调中心主界面
      window.parent.postMessage('/hjx/main', '*');
    } else if (e.data === this.shkLabel) {
      // 跳转到三河口分调中心主界面
      window.parent.postMessage('/shk/main', '*');
    }
  } else if (kind === 'onEnter' && e.data.getTag() === 'earth') {
    g3d.dm().removeScheduleTask(earthAnimation)// 删除调度
  } else if (kind === 'onLeave' && e.data.getTag() === 'earth') {
    g3d.dm().addScheduleTask(earthAnimation)// 添加调度
  }
});
