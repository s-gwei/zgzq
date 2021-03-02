//创建调度，扫煤旋转
let ljdLi = 0.5
let LjdShui = {
  interval: 300,
  action: function (data) {
    ljdLi -= 0.1
    let lianjiedong_shui = g3d.dm().getDataByTag('lianjiedong_shui')
    if (data !== lianjiedong_shui) {
      return
    }
    lianjiedong_shui.s('shape3d.uv.offset', [0, ljdLi])
  }
}

  let hsd_water_uv = 0.5
  let hsd_waterAn = {
    interval: 300,
    action (data) {
      hsd_water_uv -= 0.1
      let hsd_water = g3d.dm().getDataByTag('huangsanduan_shui')
      if (data !== hsd_water) {
        return
      }
      hsd_water.s('shape3d.uv.offset', [hsd_water_uv, 0])
    }
  }

  let dataModel = window.dataModel = new ht.DataModel();
  let g2d = window.graph = new ht.graph.GraphView(dataModel);//定义2d图纸
  let g3d = window.g3d = new ht.graph3d.Graph3dView();//定义3d模型

  g2d.setMovableFunc(() => false);

  g3d.addToDOM();
  g2d.addToDOM(g3d.getView());
  g3d.setMovableFunc(() => false);
  g3d.getBrightness = () => 1;

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

// this.event = new ht.Notifier();

  g3d.dm().clear()
  g3d.deserialize('scenes/hjx/lianjiedong/kongzhizha.json', (json, dm, gv, datas) => {
    g3d.setEye([-134.0268780549739, 345.65740615386613, 229.51843078750585])
    g3d.setCenter([-96.32281745862174, -132.3446075057357, -60.672129144655])
    g3d.dm().addScheduleTask(LjdShui)// 添加调度
    g3d.dm().addScheduleTask(hsd_waterAn)// 添加调度

    g3d.dm().getDataByTag('suidong').s({
      'wf.geometry': true
      // 'wf.color': '#32d3eb'
    }) // 可以开启线框
    g3d.dm().getDataByTag('suidong').s({
      'shape3d.transparent': true,
      'shape3d.opacity': 0.5
    }) // 可以设置透明度
  })

  g2d.dm().clear()
  g2d.deserialize('displays/hjx/lianjiedong/lianjiedong_main.json', (json, dm, g2d, datas) => {

  })

  g2d.mi(e => {
    const kind = e.kind

  });

  //双击查看
  g3d.mi(e => {
    const kind = e.kind
    if (kind === 'clickData') {
      if (e.data.getTag() === 'lianjiedongzha') {
        g3d.dm().removeScheduleTask(LjdShui)// 删除调度
        g3d.dm().removeScheduleTask(hsd_waterAn)// 删除调度
        // this.main.$router.push({
        //   name: 'turn_out_gate'
        // })
        window.parent.postMessage('/shk/connectionHole/turn_out_gate', '*');
      }
    }
  });
