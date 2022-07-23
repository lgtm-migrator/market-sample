//
//  Cache.swift
//  bnplIOSDemo
//
//  Created by Shu on 2022/07/23.
//

import Foundation
import Credify

class Cache {
    static let shared = Cache()
    
    private init() {}
    
    var currentUser: CredifyUserModel!
    
    var address: String = ""
    var tier: String = ""
    var totalAmount: Int = 0
}
