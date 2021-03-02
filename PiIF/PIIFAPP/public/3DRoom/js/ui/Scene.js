export default class Scene {
  constructor (main) {
    this.main = main
    this.g2d = main.g2d
    this.g3d = main.g3d
  }
  setUp () {
    this.g3d.mi(this.handleInteractive3d, this)
    this.g2d.mi(this.handleInteractive2d, this)
  }
  tearDown () {
    this.g3d.umi(this.handleInteractive3d, this)
    this.g2d.umi(this.handleInteractive2d, this)
  }
  handleInteractive2d () { }
  handleInteractive3d () { }
}
