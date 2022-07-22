//
//  ViewController.swift
//  bnplIOSDemo
//
//  Created by Shu on 2022/07/20.
//

import UIKit
import Credify
import Alamofire
import NVActivityIndicatorView

let API_KEY = "4ei9oQs7HqNb74khSHIp6OCDCkNmVZJ97JRnZRPy5NGeJgPtAIWYiJD1woWamzYz"
let APP_NAME = "BNPL demo market"
let APP_ID = "40f9a736-0d97-409b-a0f7-d23ebca20bde"
//let PUSH_CLAIMS_API_URL = "http://localhost:8000/v1/push-claims"
//let CREATE_ORDER_API_URL = "http://localhost:8000/v1/orders"
//let DEMO_USER_API_URL = "http://localhost:8000/v1/demo-user"
let PUSH_CLAIMS_API_URL = "https://bnpl-demo.herokuapp.com/v1/push-claims"
let CREATE_ORDER_API_URL = "https://bnpl-demo.herokuapp.com/v1/orders"
let DEMO_USER_API_URL = "https://bnpl-demo.herokuapp.com/v1/demo-user"


let MOCK_ORDER_DATA: [String: Any] = [
    "reference_id": "testtest",
    "total_amount": [
        "value": "9000000",
        "currency": "VND"
    ],
    "order_lines": [
        [
            "name": "AirPods Pro",
            "reference_id": "airpods_pro",
            "image_url": "https://www.apple.com/v/airpods/shared/compare/a/images/compare/compare_airpods_pro__e9uzt0mzviem_large_2x.png",
            "product_url": "https://www.apple.com/vn/airpods-pro/",
            "quantity": 1,
            "measment_unit": "EACH", // TYPO
            "unit_price": [
                "value": "4000000",
                "currency": "VND"
            ],
            "subtotal": [
                "value": "4000000",
                "currency": "VND"
            ]
        ],
        [
            "name": "Apple Watch 3",
            "reference_id": "apple_watch_series_three",
            "image_url": "https://www.apple.com/v/apple-watch-series-3/v/images/overview/hero__e4ykmvto2gsy_large_2x.jpg",
            "product_url": "https://www.apple.com/vn/apple-watch-series-3/",
            "quantity": 2,
            "measment_unit": "EACH", // TYPO
            "unit_price": [
                "value": "2500000",
                "currency": "VND"
            ],
            "subtotal": [
                "value": "5000000",
                "currency": "VND"
            ]
        ]
    ]
]

class ViewController: UIViewController {

    private let bnpl = serviceX.BNPL()
    private let passport = serviceX.Passport()

    private lazy var activityIndicatorView = {
        return NVActivityIndicatorView(frame: CGRect(origin: CGPoint(x: UIScreen.main.bounds.size.width * 0.5 - 40, y: UIScreen.main.bounds.size.height * 0.5 - 40), size: CGSize(width: 80, height: 80)), type: .ballRotateChase, color: .red)
    }()
    
    private var user: CredifyUserModel!
    private var orderInfo: OrderInfo!
    private var canUseBNPL: Bool = false {
        didSet {
            self.actionButton.isEnabled = canUseBNPL
        }
    }
    
    @IBOutlet private weak var productNameOneLabel: UILabel!
    @IBOutlet private weak var priceOneLabel: UILabel!
    @IBOutlet private weak var productNameTwoLabel: UILabel!
    @IBOutlet private weak var priceTwoLabel: UILabel!
    @IBOutlet private weak var priceTotalLabel: UILabel!
    @IBOutlet private weak var actionButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        view.addSubview(activityIndicatorView)
        
        let config = serviceXConfig(apiKey: API_KEY, env: .sit, appName: APP_NAME, userAgent: "servicex/ios/0.6.0")
        serviceX.configure(config)
        serviceX.setLanguage(.vietnamese)
                        
        setupUI()
        setRandomUser()
    }
    
    @IBAction func checkout(_ sender: Any) {
        createOrder { orderInfo in
            self.startBNPL(orderInfo: orderInfo)
        }
    }
    
    func setRandomUser() {
        activityIndicatorView.startAnimating()

        AF.request(DEMO_USER_API_URL)
            .responseJSON { (data) in
                DispatchQueue.main.async {
                    self.activityIndicatorView.stopAnimating()
                }
                switch data.result {
                case .success(let value):
                    guard let v = value as? [String:Any] else { return }
                    if let id = v["id"] as? Int {
                        let firstName = v["firstName"] as? String ?? ""
                        let lastName =  v["lastName"] as? String ?? ""
                        let email = v["email"] as? String ?? ""
                        let phoneNumber = v["phoneNumber"] as? String ?? ""
                        let phoneCountryCode = v["phoneCountryCode"] as? String ?? ""
                        let credifyId = v["credifyId"] as? String ?? ""
                        
                        self.user = CredifyUserModel(id: "\(id)",
                                                     firstName: firstName,
                                                     lastName: lastName,
                                                     email: email,
                                                     credifyId: credifyId,
                                                     countryCode: phoneCountryCode,
                                                     phoneNumber: phoneNumber)
                        self.getBNPLAvailability()
                    }
                case .failure(let err):
                    print(err)
                }
            }
    }
    
    func setupUI() {
        actionButton.isEnabled = false
        
        guard let orders = MOCK_ORDER_DATA["order_lines"] as? [[String: Any]] else { return }
        guard let subtotalOne = orders[0]["subtotal"] as? [String: Any] else { return }
        guard let subtotalTwo = orders[1]["subtotal"] as? [String: Any] else { return }
        guard let nameOne = orders[0]["name"] as? String else { return }
        guard let nameTwo = orders[1]["name"] as? String else { return }
        guard let quantityOne = orders[0]["quantity"] as? Int else { return }
        guard let quantityTwo = orders[1]["quantity"] as? Int else { return }
        guard let total = MOCK_ORDER_DATA["total_amount"] as? [String: Any] else { return }

        productNameOneLabel.text = "\(nameOne) (\(quantityOne))"
        productNameTwoLabel.text = "\(nameTwo) (\(quantityTwo))"
        priceOneLabel.text = "\(subtotalOne["value"] as? String ?? "") vnd"
        priceTwoLabel.text = "\(subtotalTwo["value"] as? String ?? "") vnd"
        priceTotalLabel.text = "\(total["value"] as? String ?? "") vnd"
    }
    
    /// This will check whether BNPL is available or not
    /// You need to create "orderInfo" on your side.
    func getBNPLAvailability() {
        DispatchQueue.main.async {
            self.activityIndicatorView.startAnimating()
        }
        

        bnpl.getBNPLAvailability(user: self.user) { result in
            DispatchQueue.main.async {
                self.activityIndicatorView.stopAnimating()
            }

            switch result {
            case .success((let isAvailable, let credifyId)):
                
                print(result)
                
                self.user.credifyId = credifyId
                
                if isAvailable {
                    self.canUseBNPL = true
                } else {
                    self.canUseBNPL = false
                }
                
                // BNPL is not available
            case .failure(let error):
                print(error)
            }
        }
    }
    
    func createOrder(completion: @escaping (OrderInfo) -> Void) {
        activityIndicatorView.startAnimating()

        AF.request(CREATE_ORDER_API_URL,
                   method: .post,
                   parameters: MOCK_ORDER_DATA,
                   encoding: JSONEncoding.default)
            .responseJSON { (data) in
                DispatchQueue.main.async {
                    self.activityIndicatorView.stopAnimating()
                }
                
                switch data.result {
                case .success(let value):
                    guard let v = value as? [String: Any] else { return }
                    
                    guard let id = v["id"] as? String else { return }
                    guard let amountObj = v["totalAmount"] as? [String: Any] else { return }
                    guard let amount = amountObj["value"] as? String else { return }
                    
                    // TODO: cache the order ID and amount
                    
                    let orderInfo = OrderInfo(orderId: id, orderAmount: FiatCurrency(value: amount, currency: .vnd))
                    completion(orderInfo)
                case .failure(let err):
                    print(err)
                }
            }
    }
    
    /// This starts Credify SDK
    /// You need to create "orderInfo" on your side.
    func startBNPL(orderInfo: OrderInfo) {
        let task: ((String, ((Bool) -> Void)?) -> Void) = { credifyId, result in
            // Using Alamofire
            AF.request(PUSH_CLAIMS_API_URL,
                       method: .post,
                       parameters: ["id": self.user.id, "credify_id": credifyId],
                       encoding: JSONEncoding.default).responseJSON { data in
                switch data.result {
                case .success:
                    result?(true)
                case .failure:
                    result?(false)
                }
            }
        }
        
        bnpl.presentModally(
            from: self,
            userProfile: self.user,
            orderInfo: orderInfo,
            pushClaimTokensTask: task
        ) { [weak self] status, orderId, isPaymentCompleted in
            self?.dismiss(animated: false) {
                print("Status: \(status.rawValue), order id: \(orderId), payment completed: \(isPaymentCompleted)")
            }
        }
    }
    
    func showServiceInstance() {
        passport.showDetail(
            from: self,
            user: user!,
            marketId: APP_ID,
            productTypes: []
        ) {
            print("page dismissed")
        }
    }
    
}

