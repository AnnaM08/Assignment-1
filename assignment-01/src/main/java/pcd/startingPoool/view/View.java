package pcd.startingPoool.view;


import pcd.startingPoool.controller.ActiveController;

public class View {

	private ViewFrame frame;
	private ViewModel viewModel;
	
	public View(ViewModel model, int w, int h, ActiveController controller) {
		frame = new ViewFrame(model, w, h, controller);
		frame.setVisible(true);
		this.viewModel = model;
	}
		
	public void render() {
		frame.render();
	}
	
	public ViewModel getViewModel() {
		return viewModel;
	}
}
