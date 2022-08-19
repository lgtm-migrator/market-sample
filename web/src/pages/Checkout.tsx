import React, {useEffect, useState} from 'react';
import {Layout, Col, Divider, Row, Button, Spin} from 'antd';
import AppHeader from "../components/AppHeader";
import {
  BNPLInfo,
  getBNPLInfo,
  OfferFlowPayloadBnpl,
  startOfferFlowBnpl, UserProfile
} from "credify-web-sdk";
import {API_KEY, APP_ID, CREATE_ORDER_API_URL, PUSH_CLAIMS_API_URL} from "../consts";
import axios from "axios";
import {ORDER_DATA} from "../mock";

const { Footer } = Layout;

interface Props {
  user: UserProfile;
}

function Checkout(props: Props) {

  const [loading, setLoading] = useState(false);
  const [bnplInfo, setBnplInfo] = useState<BNPLInfo | null>(null)

  useEffect(() => {
    // initialize(Environment.SIT, API_KEY)

    getBNPLInformation().then(() => {})
  }, [])

  const pushClaimsCB = async (
    externalId: string,
    resolve: (isSuccess: boolean) => void
  ) => {
    console.log("Push claim token......")
    try {
      const res = await pushClaims(props.user.id || "", externalId)
      console.log({ res })
      resolve(true)
    } catch (error) {
      resolve(false)
    }
  }

  const pushClaims = async (localId: string, credifyId: string) => {
    const body = {
      id: localId,
      credify_id: credifyId,
    }
    try {
      const res = await axios.post(PUSH_CLAIMS_API_URL, body);
      return res.data;
    } catch (error) {
      throw error
    }
  }

  const getBNPLInformation = async () => {
    setLoading(true)
    const u = props.user;
    const options = {
      phoneNumber: u.phoneNumber,
      countryCode: u.countryCode,
      credifyId: u.credifyId,
      productTypes: [],
    }
    const info = await getBNPLInfo(u.id, options)
    setLoading(false)
    setBnplInfo(info)
    console.log(info)
  }

  const createOrder = async () => {
    const res = await axios.post(CREATE_ORDER_API_URL, { local_id: props.user.id, ...ORDER_DATA })
    return res.data;
  }

  const startBNPL = (e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
    e.preventDefault()

    if (!bnplInfo) { return }
    setLoading(true)
    createOrder().then((res) => {
      const payload: OfferFlowPayloadBnpl = {
        offers: bnplInfo.offers,
        profile: props.user,
        orderId: res.id,
        orderAmount: {
          value: ORDER_DATA.total_amount.value,
          currency: "VND",
        },
        completeBnplProviders: bnplInfo.providers,
        marketId: APP_ID
      }

      setLoading(false)

      startOfferFlowBnpl(true, payload, pushClaimsCB);
    }).catch((err) => {
      setLoading(false)
      console.log(err);
    })
  }

  return (
    <Spin spinning={loading}>
      <Layout>
        <AppHeader />
        <>
          <Divider orientation="center">Checkout</Divider>
          <Row>
            <Col span={12} xs={{ order: 1 }} sm={{ order: 2 }} md={{ order: 3 }} lg={{ order: 4 }} offset={6}>
              <Col>
                {`${ORDER_DATA.order_lines[0].name} (${ORDER_DATA.order_lines[0].quantity}): ${ORDER_DATA.order_lines[0].subtotal.value} ${ORDER_DATA.order_lines[0].subtotal.currency}`}
              </Col>
              <Col>
                {`${ORDER_DATA.order_lines[1].name} (${ORDER_DATA.order_lines[1].quantity}): ${ORDER_DATA.order_lines[1].subtotal.value} ${ORDER_DATA.order_lines[1].subtotal.currency}`}
              </Col>
              <Col>
                Total price: {`${ORDER_DATA.total_amount.value} ${ORDER_DATA.total_amount.currency}`}
              </Col>
              <Col>
                <Button onClick={startBNPL}>Pay with BNPL</Button>
              </Col>
            </Col>
          </Row>
        </>
        <Footer style={{ marginTop: "16px", textAlign: "center", backgroundColor: "#F6E6D8" }}>This is Demo Store for serviceX BNPL</Footer>
      </Layout>
    </Spin>
  )

}

export default Checkout;
