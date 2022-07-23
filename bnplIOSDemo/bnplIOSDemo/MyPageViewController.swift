//
//  MyPageViewController.swift
//  bnplIOSDemo
//
//  Created by Shu on 2022/07/21.
//

import UIKit
import Credify
import Alamofire
import NVActivityIndicatorView

class MyPageViewController: UIViewController {

    @IBOutlet private weak var idLabel: UILabel!
    @IBOutlet private weak var firstNameLabel: UILabel!
    @IBOutlet private weak var lastNameLabel: UILabel!
    @IBOutlet private weak var emailLabel: UILabel!
    @IBOutlet private weak var phoneLabel: UILabel!
    @IBOutlet private weak var addressLabel: UILabel!
    @IBOutlet private weak var tierLabel: UILabel!
    @IBOutlet private weak var totalPointLabel: UILabel!
    @IBOutlet private weak var credifyIdLabel: UILabel!
    
    private let passport = serviceX.Passport()

    private lazy var activityIndicatorView = {
        return NVActivityIndicatorView(frame: CGRect(origin: CGPoint(x: UIScreen.main.bounds.size.width * 0.5 - 40, y: UIScreen.main.bounds.size.height * 0.5 - 40), size: CGSize(width: 80, height: 80)), type: .ballRotateChase, color: .red)
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        view.addSubview(activityIndicatorView)
        
        mapData()
    }
    
    @IBAction func refresh(_ sender: Any) {
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
                        
                        // Cache additional data
                        if let a = v["address"] as? String, let tier = v["tier"] as? String, let amount = v["loyaltyPoint"] as? Int {
                            Cache.shared.address = a
                            Cache.shared.tier = tier
                            Cache.shared.totalAmount = amount
                        }
                        
                        Cache.shared.currentUser = CredifyUserModel(id: "\(id)",
                                                     firstName: firstName,
                                                     lastName: lastName,
                                                     email: email,
                                                     credifyId: credifyId,
                                                     countryCode: phoneCountryCode,
                                                     phoneNumber: phoneNumber)
                        self.mapData()
                    }
                case .failure(let err):
                    print(err)
                }
            }
    }
    
    @IBAction func showCredifyPassport(_ sender: Any) {
        let task: ((String, ((Bool) -> Void)?) -> Void) = { credifyId, result in
            Cache.shared.currentUser.credifyId = credifyId
            AF.request(PUSH_CLAIMS_API_URL,
                       method: .post,
                       parameters: ["id": Cache.shared.currentUser.id, "credify_id": credifyId],
                       encoding: JSONEncoding.default).responseJSON { data in
                switch data.result {
                case .success:
                    result?(true)
                case .failure:
                    result?(false)
                }
            }
        }
        passport.showMypage(from: self, user: Cache.shared.currentUser, pushClaimTokensTask: task) {
            print("page dismissed")
        }
    }
    
    @IBAction func showBNPL(_ sender: Any) {
        passport.showDetail(
            from: self,
            user: Cache.shared.currentUser,
            marketId: APP_ID,
            productTypes: [.consumerBNPL]
        ) {
            print("page dismissed")
        }
    }
    
    func mapData() {
        let u = Cache.shared.currentUser
        idLabel.text = u?.id
        firstNameLabel.text = u?.firstName
        lastNameLabel.text = u?.lastName
        emailLabel.text = u?.email
        phoneLabel.text = "\(u?.countryCode ?? "") \(u?.phoneNumber ?? "")"
        addressLabel.text = Cache.shared.address
        tierLabel.text = Cache.shared.tier
        totalPointLabel.text = "\(Cache.shared.totalAmount)"
        credifyIdLabel.text = u?.credifyId ?? ""
    }

}
