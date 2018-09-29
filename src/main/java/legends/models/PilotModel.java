package legends.models;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class PilotModel {

	private final int photoID;
	private final int extraID;

	public PilotModel(int photoID, int extraID) {
		this.photoID = photoID;
		this.extraID = extraID;
	}

	public static List<Integer> getPilotPairIndexes(@NotNull List<PilotModel> models) {

		final ArrayList<Integer> pilotTasks = new ArrayList<>(models.size() * 2);
		for (final PilotModel model : models) {
			pilotTasks.add(model.photoID);
			pilotTasks.add(model.extraID);
		}
		return pilotTasks;
	}
}
