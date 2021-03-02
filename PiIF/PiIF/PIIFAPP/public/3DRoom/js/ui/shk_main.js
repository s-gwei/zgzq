
let dataModel = window.dataModel = new ht.DataModel();
let g2d = window.graph = new ht.graph.GraphView(dataModel);//定义2d图纸
let g3d = window.g3d = new ht.graph3d.Graph3dView();//定义3d模型

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
g2d.deserialize('displays/hjx/shk_mainGraph.json',(json,dm,g3d,datas) =>{
  // 反序列化图纸完成回调
});//反序列化图纸


g3d.deserialize('scenes/hjx/daba/daba.json',(json,dm,g3d,datas) =>{
  g3d.moveCamera([-1183.9876398832498, 357.6963116564919, 2281.5006157858456],
    [-48.09865074151396, -13.688348751436603, 1487.2650853592036],
    true)
  var mapInteractor = new ht.graph3d.MapInteractor(g3d)
  g3d.setInteractors([ mapInteractor ])
});//反序列化3d

g2d.mi(e => {

});

//双击查看
g3d.mi(e => {
  const kind = e.kind
  if (kind === 'clickData') {
    if (e.data.getTag() === 'ljd') {
      // this.main.$router.push({
      //   name: 'connection_main'
      // })
      window.parent.postMessage('/shk/connectionHole/main', '*');
      // window.location.href='./lianjiedong_main.html';
    }
  }
});
