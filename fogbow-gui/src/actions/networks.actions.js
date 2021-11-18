import { toast } from 'react-toastify';

import { messages, getErrorMessage } from '../defaults/messages';
import { networksActionsTypes } from './networks.actions.types';
import NetworksProvider from '../providers/networks.provider';
import ResourceActions from './common.actions';

export const getAllNetworkAllocation = (providerId, cloudNames) => {
  let resourceProvider = new NetworksProvider();
  const request = () => ({ type: networksActionsTypes.GET_ALL_NETWORK_ALLOCATION_REQUEST});
  const success = (allocations) => ({ type: networksActionsTypes.GET_ALL_NETWORK_ALLOCATION_SUCCESS, allocations });
  const failure = (error) => ({ type: networksActionsTypes.GET_ALL_NETWORK_ALLOCATION_FAILURE, error });
  const actionTypes = { request, success, failure };
  return dispatch => ResourceActions.getAllocations(providerId, cloudNames, dispatch, resourceProvider, actionTypes);
};

export const getNetworkAllocation = (providerId, cloudName) => {
  let resourceProvider = new NetworksProvider();
  const request = () => ({ type: networksActionsTypes.GET_NETWORK_ALLOCATION_REQUEST});
  const success = (allocation) => ({ type: networksActionsTypes.GET_NETWORK_ALLOCATION_SUCCESS, allocation });
  const failure = (error) => ({ type: networksActionsTypes.GET_NETWORK_ALLOCATION_FAILURE, error });
  const actionTypes = { request, success, failure };
  return dispatch => ResourceActions.getAllocation(providerId, cloudName, dispatch, resourceProvider, actionTypes);
};

export const getNetworks = () => {
  let provider = new NetworksProvider();
  const request = () => ({ type: networksActionsTypes.GET_NETWORKS_REQUEST});
  const success = (networks) => ({ type: networksActionsTypes.GET_NETWORKS_SUCCESS, networks });
  const failure = (error) => ({ type: networksActionsTypes.GET_NETWORKS_FAILURE, error });
  const actionTypes = { request, success, failure };
  return (dispatch) => ResourceActions.listAll(dispatch, provider, actionTypes)
};

export const getNetworkData = (id) => {
  let provider = new NetworksProvider();
  const request = () => ({ type: networksActionsTypes.GET_DATA_NETWORK_REQUEST});
  const success = (networks) => ({ type: networksActionsTypes.GET_DATA_NETWORK_SUCCESS, networks });
  const failure = (error) => ({ type: networksActionsTypes.GET_DATA_NETWORK_FAILURE, error });
  const actionTypes = { request, success, failure };
  return (dispatch) => ResourceActions.get(id, dispatch, provider, actionTypes)
};

export const createNetwork = (body) => {
  let provider = new NetworksProvider();
  const request = () => ({ type: networksActionsTypes.CREATE_NETWORK_REQUEST});
  const success = (network) => ({ type: networksActionsTypes.CREATE_NETWORK_SUCCESS, network, provider: body.provider });
  const failure = (error) => ({ type: networksActionsTypes.CREATE_NETWORK_FAILURE, error });
  const actionTypes = { request, success, failure };
  return dispatch => ResourceActions.create(body, dispatch, provider, actionTypes);
};

export const deleteNetwork = (id) => {
  let provider = new NetworksProvider();
  const request = () => ({ type: networksActionsTypes.DELETE_NETWORK_REQUEST});
  const success = () => ({ type: networksActionsTypes.DELETE_NETWORK_SUCCESS, id });
  const failure = (error) => ({ type: networksActionsTypes.DELETE_NETWORK_FAILURE, error });
  const actionTypes = { request, success, failure };
  return (dispatch) => ResourceActions.delete(id, dispatch, provider, actionTypes)
};

export const getFedNetworks = () => {
  return dispatch => {
    return new Promise((resolve, reject) => {
      let provider = new NetworksProvider();
      const request = () => ({ type: networksActionsTypes.GET_FED_NETWORKS_REQUEST});
      const success = (networks) => ({ type: networksActionsTypes.GET_FED_NETWORKS_SUCCESS, networks });
      const failure = (error) => ({ type: networksActionsTypes.GET_FED_NETWORKS_FAILURE, error });

      dispatch(request());

      provider.getFetNets().then(
        network => resolve(dispatch(success(network.data)))
      ).catch((error) => {
        const message = getErrorMessage(error);
        toast.error(messages.orders.getStatus.concat(message));
        return reject(dispatch(failure(error)));
      });
    });
  };
};

export const getFedNetworkData = (id) => {
  return dispatch => {
    return new Promise((resolve, reject) => {
      let provider = new NetworksProvider();
      const request = () => ({ type: networksActionsTypes.GET_FED_DATA_NETWORK_REQUEST});
      const success = (networks) => ({ type: networksActionsTypes.GET_FED_DATA_NETWORK_SUCCESS, networks });
      const failure = (error) => ({ type: networksActionsTypes.GET_FED_DATA_NETWORK_FAILURE, error });

      dispatch(request());

      provider.getFedNetData(id).then(
        network => resolve(dispatch(success(network.data)))
      ).catch((error) => {
        const message = getErrorMessage(error);
        toast.error(messages.orders.get.concat(id, message));
        return reject(dispatch(failure(error)));
      });
    });
  };
};

export const createFedNetwork = (body) => {
  return dispatch => {
    return new Promise((resolve, reject) => {
      let provider = new NetworksProvider();
      const request = () => ({ type: networksActionsTypes.CREATE_FED_NETWORK_REQUEST});
      const success = (network) => ({ type: networksActionsTypes.CREATE_FED_NETWORK_SUCCESS, network });
      const failure = (error) => ({ type: networksActionsTypes.CREATE_FED_NETWORK_FAILURE, error });

      dispatch(request());

      provider.createFedNet(body).then(
        network => resolve(dispatch(success(network.data.id)))
      ).catch((error) => {
        const message = getErrorMessage(error);
        toast.error(messages.orders.create.concat(message));
        return reject(dispatch(failure(error)));
      });
    });
  };
};

export const deleteFedNetwork = (id) => {
  return dispatch => {
    return new Promise((resolve, reject) => {
      let provider = new NetworksProvider();
      const request = () => ({ type: networksActionsTypes.DELETE_FED_NETWORK_REQUEST});
      const success = () => ({ type: networksActionsTypes.DELETE_FED_NETWORK_SUCCESS, id });
      const failure = (error) => ({ type: networksActionsTypes.DELETE_FED_NETWORK_FAILURE, error });

      dispatch(request());

      provider.deletefedNet(id).then(
        resolve(dispatch(success()))
      ).catch((error) => {
        const message = getErrorMessage(error);
        toast.error(messages.orders.remove.concat(id, message));
        return reject(dispatch(failure(error)));
      });
    });
  };
};

export const createNetworkSecurityRule = (body, id) => {
  return dispatch => {
    return new Promise((resolve, reject) => {
      let provider = new NetworksProvider();
      const request = () => ({ type: networksActionsTypes.CREATE_FED_NETWORK_REQUEST});
      const success = (network) => ({ type: networksActionsTypes.CREATE_FED_NETWORK_SUCCESS, network });
      const failure = (error) => ({ type: networksActionsTypes.CREATE_FED_NETWORK_FAILURE, error });

      dispatch(request());

      provider.createSecurityRule(body, id).then(
        securityRule => resolve(dispatch(success(securityRule.data.id)))
      ).catch((error) => {
        const message = getErrorMessage(error);
        toast.error(messages.securityRules.create.concat(message));
        return reject(dispatch(failure(error)));
      });
    });
  };
};

export const getNetworkSecurityRules = (id) => {
  return dispatch => {
    return new Promise((resolve, reject) => {
      let provider = new NetworksProvider();
      const request = () => ({ type: networksActionsTypes.GET_NETWORK_SECURITY_RULES_REQUEST});
      const success = (securityRules) => ({
        type: networksActionsTypes.GET_NETWORK_SECURITY_RULES_SUCCESS,
        securityRules: securityRules
      });
      const failure = (error) => ({
        type: networksActionsTypes.GET_NETWORK_SECURITY_RULES_FAILURE,
        error: error
      });

      dispatch(request());

      provider.getSecurityRules(id).then(
        securityRules => resolve(dispatch(success(securityRules.data)))
      ).catch((error) => {
        const message = getErrorMessage(error);
        toast.error(messages.securityRules.get.concat(id, message));
        return reject(dispatch(failure(error)));
      });
    });
  };
};

export const deleteNetworkSecurityRule = (ruleId, orderId) => {
  return dispatch => {
    return new Promise((resolve, reject) => {
      let provider = new NetworksProvider();
      const request = () => ({ type: networksActionsTypes.DELETE_NETWORK_SECURITY_RULE_REQUEST});
      const success = () => ({
        type: networksActionsTypes.DELETE_NETWORK_SECURITY_RULE_SUCCESS,
        ruleId: ruleId
      });
      const failure = (error) => ({
        type: networksActionsTypes.DELETE_NETWORK_SECURITY_RULE_FAILURE,
        error: error
      });

      dispatch(request());

      provider.deleteSecurityRule(ruleId, orderId).then(
        () => resolve(dispatch(success()))
      ).catch((error) => {
        const message = getErrorMessage(error);
        toast.error(messages.securityRules.remove.concat(ruleId, message));
        return reject(dispatch(failure(error)));
      });
    });
  };
};
