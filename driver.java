public class Driver {
	private Context context;
	private String weight_in_path;
	private String weight_out_path;
	//private Map<Integer, String> model_path;
	private Map<Integer, SparseVector> weights;
	private Map<Integer, Double> losses;
	private Map<Integer, Integer> status; //status of all the models
	/*
	* status = 0
	*/
	private void setup() {
	
	}

	public void run() {
		//initialize
		for(int iter_num = 0; iter_num < context.getInteger("MAX_ITER_NUM", 100); ++iter_num) {
			//init_weight
			weights = CommonFunc.readSparseVectorMap(weight_in_path);
			
			//
			
		}
	}
}
	