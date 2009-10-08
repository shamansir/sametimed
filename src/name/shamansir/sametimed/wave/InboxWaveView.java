package name.shamansir.sametimed.wave;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.shamansir.sametimed.wave.model.base.atom.InboxElement;

import org.waveprotocol.wave.examples.fedone.common.CommonConstants;
import org.waveprotocol.wave.examples.fedone.common.HashedVersion;
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientUtils;
import org.waveprotocol.wave.examples.fedone.waveclient.common.ClientWaveView;
import org.waveprotocol.wave.examples.fedone.waveclient.common.IndexEntry;

/**
 * 
 * @author shamansir <shaman.sir@gmail.com>
 * 
 * Inbox View, generates the representation of Inbox in the Map<Integer, InboxElement> form,
 * using the ClientWaveView as source. Also, holds and controls the current state
 * of the Inbox. (must be Controller in MVC terms, though)
 * 
 * @see WavesClientBackend
 * @see ClientWaveView
 * @see InboxElement
 * 
 */

// based on ScrollableInbox/ScrollableWaveView class
public class InboxWaveView {

	private ClientWaveView indexWave;
	private WavesClientBackend backend;
	private Object openedWave = null;
	private final Map<ClientWaveView, HashedVersion> lastSeenVersions = new HashMap<ClientWaveView, HashedVersion>();	

	public InboxWaveView(WavesClientBackend backend, ClientWaveView indexWave) {
	    if (!indexWave.getWaveId().equals(CommonConstants.INDEX_WAVE_ID)) {
	        throw new IllegalArgumentException(indexWave + " is not an index wave");
	    }

	    this.indexWave = indexWave;
	    this.backend = backend;
	}

	public void setOpenWave(ClientWaveView openedWave) {
	    this.openedWave = openedWave;		
	}

	public void updateHashedVersions() {
	    for (IndexEntry indexEntry : ClientUtils.getIndexEntries(indexWave)) {
	        ClientWaveView wave = backend.getWave(indexEntry.getWaveId());
	        if ((wave != null) && (ClientUtils.getConversationRoot(wave) != null)) {
	          lastSeenVersions.put(wave, wave.getWaveletVersion(ClientUtils.getConversationRootId(wave)));
	        }
	    }
	}

	public Map<Integer, InboxElement> getOpenedWaves() {
		Map<Integer, InboxElement> inboxData = new HashMap<Integer, InboxElement>();

		List<IndexEntry> indexEntries = ClientUtils.getIndexEntries(indexWave);

		for (int i = 0; i < indexEntries.size(); i++) {
			ClientWaveView wave = backend.getWave(indexEntries.get(i)
					.getWaveId());

			boolean isCurrentWave = false;
			boolean isNewWave = false;
			String waveId = "";
			String waveDigest = "";

			if ((wave == null)
					|| (ClientUtils.getConversationRoot(wave) == null)) {
				waveId = "...";
				waveDigest = "...";
			} else {
				HashedVersion version = wave.getWaveletVersion(ClientUtils
						.getConversationRootId(wave));

				waveId = wave.getWaveId().getId();
				waveDigest = indexEntries.get(i).getDigest();

				if (wave == openedWave) {
					isCurrentWave = true;
					lastSeenVersions.put(wave, version);
				} else if (!version.equals(lastSeenVersions.get(wave))) {
					isNewWave = true;
				}
			}
			// TODO: also store version			
			inboxData.put(Integer.valueOf(i), new InboxElement(waveId,
					waveDigest, isCurrentWave, isNewWave));
		}

		return inboxData;
	    
	}

}
