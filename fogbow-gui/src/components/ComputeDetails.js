import React, { Component } from 'react';
import { connect } from 'react-redux';
import _ from 'lodash';

import { getComputeData } from '../actions/computes.actions';

class ComputeDetails extends Component {
  constructor(props) {
    super(props);
    this.state = {
      orderData: {}
    }
  }

  componentDidMount() {
    let { dispatch } = this.props;
    dispatch(getComputeData(this.props.id)).then(data => {
      this.setState({
        orderData: data.compute
      });
    })
  }

  render() {
    const networks = this.state.orderData.networks ? this.state.orderData.networks
      .map((network, idx) =>
        <div key={network.id}>
          <p>Name: {network.name}</p>
          <p>ID: {network.id}</p>
        </div>)
      : <p>-</p>;

    const userData = this.state.orderData.userData && this.state.orderData.userData.length > 0 ?
      Object.values(this.state.orderData.userData).map(
        (data, idx) =>
          <div key={idx}>
            <p>Tag: {data.tag || '-'}</p>
            <p>Type: {data.extraUserDataFileType || '-'}</p>
          </div>
        )
      : <p>-</p>;

    return (
      <div className="details">
        <button type="button" className="close" aria-label="Close"
                onClick={() => this.props.handleHide()}>
          <span aria-hidden="true">&times;</span>
        </button>

        <h2>Compute Details</h2>
        <hr className="horizontal-line"/>

        <p className="bolder">Name</p>
        <p>{this.state.orderData.name || '-'}</p>

        <p className="bolder">ID</p>
        <p>{this.state.orderData.id || '-'}</p>

        <p className="bolder">State</p>
        <p>{this.state.orderData.state || '-'}</p>

        <p className="bolder">Fault Message</p>
        <p>{this.state.orderData.faultMessage || '-'}</p>

        <p className="bolder">VCPUs</p>
        <p>{this.state.orderData.vCPU || '-'}</p>

        <p className="bolder">RAM</p>
        <p>{this.state.orderData.ram || '-'} MB</p>

        <p className="bolder">Disk</p>
        <p>{this.state.orderData.disk || '-'} GB</p>

        <p className="bolder">Networks</p>
        {networks}

        <p className="bolder">IP Addresses</p>
        <p>{_.join(this.state.orderData.ipAddresses, ',') || '-'}</p>

        <p className="bolder">Image ID</p>
        <p>{this.state.orderData.imageId || '-'}</p>

        <p className="bolder">User Data</p>
        {userData}

        <p className="bolder">Public Key</p>
        <textarea className="public-key" cols="70" rows="5"
                  value={this.state.orderData.publicKey || '-'} readOnly></textarea>
      </div>
    );
  }
}

export default connect()(ComputeDetails);
