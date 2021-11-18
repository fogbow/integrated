import { networksActionsTypes } from '../actions/networks.actions.types';

const fedNetworks = (state = {loading: false, data: []}, action) => {
    switch (action.type) {
        // GET ALL
        case networksActionsTypes.GET_FED_NETWORKS_REQUEST:
            return { ...state, loading: false };
        case networksActionsTypes.GET_FED_NETWORKS_SUCCESS:
            return {
                data: action.networks,
                loading: true
            };
        case networksActionsTypes.GET_FED_NETWORKS_FAILURE:
            return { ...state, error: action.error };
        
        // CREATE
        case networksActionsTypes.CREATE_FED_NETWORK_REQUEST:
            return { ...state, loading: false };
        case networksActionsTypes.CREATE_FED_NETWORK_SUCCESS:
            state.data.push({
                instanceId: action.network,
                state: 'OPEN',
                provider: action.provider
            });
            return {
                ...state,
                data: state.data,
                loading: true
            };
        case networksActionsTypes.CREATE_FED_NETWORK_FAILURE:
            return { ...state, error: action.error };

        // DELETE
        case networksActionsTypes.DELETE_FED_NETWORK_REQUEST:
            return { ...state };
        case networksActionsTypes.DELETE_FED_NETWORK_SUCCESS:
            return {
                ...state,
                data: state.data.filter(network => network.instanceId !== action.id),
                loading: true
            };
        case networksActionsTypes.DELETE_FED_NETWORK_FAILURE:
            return { ...state, error: action.error };
        
        default:
            return state;
    }
};

export default fedNetworks;