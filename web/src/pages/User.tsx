import React from 'react';
import {Layout, Col, Divider, Row, Button } from 'antd';
import AppHeader from "../components/AppHeader";
import {PRODUCT_TYPE, startServiceInstanceFlow, UserProfile} from "credify-web-sdk";
import {APP_NAME} from "../consts";

const { Footer } = Layout;

interface Props {
  user: UserProfile;
  loadNewUser: () => void;
}

function User(props: Props) {

  const openBNPLDetail = () => {
    startServiceInstanceFlow(APP_NAME, props.user, [
      PRODUCT_TYPE.CONSUMER_FINANCING__UNSECURED_LOAN__BNPL,
    ])
  }

  return (
    <>
      <Layout>
        <AppHeader />
        <>
          <Divider orientation="center">User</Divider>
          <Row>
            <Col span={12} xs={{ order: 1 }} sm={{ order: 2 }} md={{ order: 3 }} lg={{ order: 4 }} offset={6}>
              <Col>
                First name: <b>{props.user.firstName}</b>
              </Col>
              <Col>
                Last name: <b>{props.user.lastName}</b>
              </Col>
              <Col>
                Email: <b>{props.user.email}</b>
              </Col>
              <Col>
                Phone: <b>{props.user.countryCode} {props.user.phoneNumber}</b>
              </Col>
              <Col>
                {/* @ts-ignore */}
                Tier: <b>{props.user.tier}</b>
              </Col>
              <Col>
                {/* @ts-ignore */}
                Total point: <b>{props.user.loyaltyPoint}</b>
              </Col>
              <Col>
                Credify ID: <b>{props.user.credifyId}</b>
              </Col>
              <Col style={{ marginTop: "8px" }}>
                <Button onClick={() => props.loadNewUser()}>Refresh User</Button>
              </Col>
              <Col style={{ marginTop: "8px" }}>
                <Button onClick={() => openBNPLDetail()}>Check BNPL usage</Button>
              </Col>
            </Col>
          </Row>
        </>
        <Footer style={{ marginTop: "16px", textAlign: "center", backgroundColor: "#F6E6D8" }}>This is Demo Store for serviceX BNPL</Footer>
      </Layout>
    </>
  )
}

export default User;
