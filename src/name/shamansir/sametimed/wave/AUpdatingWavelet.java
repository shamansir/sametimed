package name.shamansir.sametimed.wave;

import java.util.HashSet;
import java.util.Set;

import name.shamansir.sametimed.wave.messaging.IUpdatesListener;
import name.shamansir.sametimed.wave.messaging.UpdateMessage;
import name.shamansir.sametimed.wave.model.WaveletModel;

import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientWaveView;
import org.waveprotocol.wave.examples.fedone.waveclient.console.ScrollableWaveView.RenderMode;
import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.wave.ParticipantId;
import org.waveprotocol.wave.model.wave.data.WaveletData;

// FIXME: move here all code, not related to documents
// FIXME: remove methods related to documents

public abstract class AUpdatingWavelet {
	
	public static final String NOT_CONNECTED_ERR = "not connected, run \"/connect user@domain server port\"";
	public static final String NOT_OPENED_WAVE_ERR = "Error: no wave is open, run \"/open index\"";
	
	private Set<IUpdatesListener> updatesListeners = new HashSet<IUpdatesListener>();	
	
	protected abstract boolean onParticipantsUpdated();
	
	protected abstract boolean onParticipantRemoved(WaveId waveId, ParticipantId removedUser, boolean isCurrentUser);
	
	protected abstract boolean onWavesRead();
	
	protected abstract boolean onOpenWave(ClientWaveView openedWave);
	
	protected abstract boolean onDeltaSequenceEnd(WaveletData wavelet, boolean waveOpened);
	
	protected abstract boolean onUndoCall(ParticipantId userId);
	
	protected abstract boolean onDocumentAppendMutation(String text, ParticipantId author);
	
	protected abstract boolean addParticipant(ParticipantId userId);
	
	protected abstract boolean removeParticipant(ParticipantId userId);
	
	protected abstract boolean setViewMode(RenderMode viewMode);
	
	protected abstract void registerError(String error);
	
	protected abstract void clear();
	
	protected abstract void initWith(WavesProvider wavesProvider, WaveletOperationsSender opsSender, String userInfo, String hostInfo); 
	
	public abstract WaveletModel getModel(); // FIXME: move model operations inside
	
	public void addUpdatesListener(IUpdatesListener listener) {
		synchronized (updatesListeners) {
			updatesListeners.add(listener);
		}
	}
	
	protected void dispatchUpdate(UpdateMessage updateMessage) {
		synchronized (updatesListeners) {
			for(IUpdatesListener listener: updatesListeners) {
				listener.onUpdate(updateMessage);
			}
		}
		
	}
}
