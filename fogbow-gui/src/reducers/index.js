import { combineReducers } from 'redux';

import providers from './providers.reducer';
import computes from './computes.reducer';
import volumes from './volumes.reducer';
import networks from './networks.reducer';
import fedNetworks from './fedNetworks.reducer';
import attachments from './attachments.reducer';
import images from './images.reducer';
import remoteImages from './remoteImages.reducer';
import quota from './quota.reducer';
import publicIps from './publicIp.reducer';
import version from './version.reducer';
import securityRules from './securityRules.reducer';
import clouds from './clouds.reducer';
import remoteClouds from './remoteClouds.reducer';

export default combineReducers({
  providers,
  computes,
  volumes,
  networks,
  fedNetworks,
  attachments,
  images,
  remoteImages,
  quota,
  publicIps,
  version,
  securityRules,
  clouds,
  remoteClouds
});
