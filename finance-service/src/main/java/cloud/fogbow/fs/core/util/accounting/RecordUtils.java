package cloud.fogbow.fs.core.util.accounting;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import cloud.fogbow.accs.api.http.response.Record;
import cloud.fogbow.accs.core.models.OrderStateHistory;
import cloud.fogbow.accs.core.models.specs.ComputeSpec;
import cloud.fogbow.accs.core.models.specs.VolumeSpec;
import cloud.fogbow.common.exceptions.InvalidParameterException;
import cloud.fogbow.fs.constants.Messages;
import cloud.fogbow.fs.core.models.ComputeItem;
import cloud.fogbow.fs.core.models.ResourceItem;
import cloud.fogbow.fs.core.models.VolumeItem;
import cloud.fogbow.ras.core.models.orders.OrderState;


public class RecordUtils {

    public ResourceItem getItemFromRecord(Record record) throws InvalidParameterException {
        String resourceType = record.getResourceType();
        ResourceItem item;
        
        if (resourceType.equals(ComputeItem.ITEM_TYPE_NAME)) {
            ComputeSpec spec = (ComputeSpec) record.getSpec();
            item = new ComputeItem(spec.getvCpu(), spec.getRam());
        } else if (resourceType.equals(VolumeItem.ITEM_TYPE_NAME)) {
            VolumeSpec spec = (VolumeSpec) record.getSpec();
            item = new VolumeItem(spec.getSize());
        } else {
            throw new InvalidParameterException(String.format(Messages.Exception.UNKNOWN_RESOURCE_ITEM_TYPE, 
                    resourceType));
        }
        
        return item;
    }

    /**
     * Returns a NavigableMap containing the state changes that occurred in the given period of time.
     * Also, containing two extra entries, used to make processing the map easier. The first extra entry
     * maps the paymentStartTime to the state where the Record was in the beginning of the interval. The second
     * extra entry maps the paymentEndTime to the state where the Record was at the end of the interval.
     * 
     * @param record the record to process.
     * @param paymentStartTime the beginning of the interval.
     * @param paymentEndTime the end of the interval.
     * @return A NavigableMap containing the state changes.
     * @throws InvalidParameterException If the Record contains an invalid field.
     */
    public NavigableMap<Timestamp, OrderState> getRecordStateHistoryOnPeriod(Record record, Long paymentStartTime,
            Long paymentEndTime) throws InvalidParameterException {
        OrderStateHistory orderHistory = record.getStateHistory();
        Map<Timestamp, cloud.fogbow.accs.core.models.orders.OrderState> accsHistory = orderHistory.getHistory();

        Map<Timestamp, OrderState> history = convertAccountingStateToRasState(accsHistory);
        return filterStatesByPeriod(history, paymentStartTime, paymentEndTime);        
    }

    // This code must be removed after removing the duplicated Order classes in ACCS
	private Map<Timestamp, OrderState> convertAccountingStateToRasState(
			Map<Timestamp, cloud.fogbow.accs.core.models.orders.OrderState> accsHistory) throws InvalidParameterException {
		Map<Timestamp, OrderState> convertedMap = new HashMap<Timestamp, OrderState>();
		
		for (Timestamp key : accsHistory.keySet()) {
			cloud.fogbow.accs.core.models.orders.OrderState accsState = accsHistory.get(key);
			convertedMap.put(key, OrderState.fromValue(accsState.getRepr()));
		}
		
		return convertedMap;
	}	

	private NavigableMap<Timestamp, OrderState> filterStatesByPeriod(Map<Timestamp, OrderState> history, Long paymentStartTime,
			Long paymentEndTime) throws InvalidParameterException {
		Timestamp lowestStateChangeTimestamp = getLowerLimit(history, paymentStartTime);
		Timestamp highestStateChangeTimestamp = getHighestTimestampBeforeTime(history, paymentEndTime);

		if (lowestStateChangeTimestamp == null) {
			throw new InvalidParameterException(Messages.Exception.INVALID_RECORD_HISTORY);
		} else {
			TreeMap<Timestamp, OrderState> filteredState = new TreeMap<Timestamp, OrderState>();
			OrderState startState = history.get(lowestStateChangeTimestamp);
			OrderState endState = history.get(highestStateChangeTimestamp);
			
			// if the first state change occurs before the period start time, then the Record
			// was created in the period and the history must start in the first state change time
			// (since there is no state to account for before the first change).
			// Otherwise, the history must start in the period start time.
			Timestamp periodLowestLimit = lowestStateChangeTimestamp.getTime() < paymentStartTime ? 
			        new Timestamp(paymentStartTime) : lowestStateChangeTimestamp;
			filteredState.put(periodLowestLimit,startState);

			// if the order was closed in the period, then CLOSED must be the last state in the history
			// and the remaining time in the period must not be considered for billing.
			if (endState != OrderState.CLOSED) {
			    filteredState.put(new Timestamp(paymentEndTime), endState);			    
			}
			
			for (Timestamp timestamp : history.keySet()) {
				if (timestamp.getTime() >= paymentStartTime 
						&& timestamp.getTime() < paymentEndTime) {
					filteredState.put(timestamp, history.get(timestamp));
				}
			}
			
			return filteredState;
		}
	}

    private Timestamp getLowerLimit(Map<Timestamp, OrderState> history, Long paymentStartTime) {
        Timestamp lowest = getHighestTimestampBeforeTime(history, paymentStartTime);
        
        if (lowest != null) {
            return lowest;
        } else { 
            return getLowestTimestamp(history.keySet());
        }
    }

	private Timestamp getLowestTimestamp(Set<Timestamp> keySet) {
	    Timestamp lowest = null;
	    
        for (Timestamp timestamp : keySet) {
            if (lowest == null) {
                lowest = timestamp;
            } else {
                if (timestamp.getTime() < lowest.getTime()) {
                    lowest = timestamp;
                }
            }
        }
        
        return lowest;
    }

    private Timestamp getHighestTimestampBeforeTime(Map<Timestamp, OrderState> history, Long time) {
		Timestamp highestTimestamp = null;
		
		for (Timestamp timestamp : history.keySet()) {
			if (timestamp.getTime() < time) {
				if (highestTimestamp == null) {
					highestTimestamp = timestamp;
				} else if (timestamp.getTime() > highestTimestamp.getTime()) {
				    highestTimestamp = timestamp;
				}
			}
		}
		
		return highestTimestamp;
	}
}
